# This python script allows a user to manually (via the command line) add documents to the vector database.

# The command line arguments are
# --input: Input file path. If a folder, all files in the folder will be uploaded. If a file, only that file. (Type: string, required)
# --use-current-time: If set, created_at time in the post request will be the current time. Otherwise, it will be the file's created date. (Type: bool)
# --test-run: If set, all commands will be printed, but not run. (Type: bool)
# --author: Sets the author to be included in the metadata of the upsert file post request. (Type: string)
# --source: Sets the file source to be included in the metadata of the upsert file post request. (Type: string)

import argparse
import requests
import os.path
import json
import time
import datetime

from extract_sentences import extract_text_from_docx_file, split_text_into_sentences

# Sentences to prune from files, because I repeat them for almost every cover letter and they don't add anything
PRUNE_DATA = ["Ethan Frank", "Sincerely,", "ethandf2@gmail.com  (484) 343-6676"]


def send_upsert_request(file_path, metadata, use_current_time=False, test_run=False):
    """
    Splits the document into sentences, and uploads each sentence separatly to the database.
    This allows for more fine grained searching (I hope)
    """
    bearerToken = os.environ['BEARER_TOKEN']

    url = 'http://0.0.0.0:8000/upsert'
    headers = {'Authorization': f'Bearer {bearerToken}'}

    # Round time to nearest second and add to metadata
    # (Note this is actually the last modified time for files)
    created_time = int(time.time() if use_current_time else os.path.getmtime(file_path))
    created_time_string = datetime.datetime.fromtimestamp(created_time).isoformat()
    metadata["created_at"] = created_time_string

    # Add file name as source ID. Note this is not necesarrily unique
    file_name = os.path.basename(file_path)
    metadata["source_id"] = file_name

    text = extract_text_from_docx_file(file_path)
    sentences = split_text_into_sentences(text)

    sentences = [s for s in sentences if s not in PRUNE_DATA]

    # Build up the request. The request is a list of "documents", each document has text, id (optional) and metadata
    request_data = []
    for sentence in sentences:
        document = {"text": sentence, "metadata": metadata}
        request_data.append(document)

    data = {"documents": request_data}

    # Send the POST request, or print that it would be sent in test run mode
    if not test_run:
        # The request expects a type of application/json, so we use request's json parameter
        response = requests.post(url, headers=headers, json=data)
        # Print the response
        print(response)
        print(response.text)
    else:
        print("Executing in test run mode. Commands will not be called")
        print(f"URL: {url}")
        print(f"Headers: {headers}")
        print(f"Data: {data}")
        print()


def send_upsert_file_request(file_path, metadata, use_current_time=False, test_run=False):
    """
    Sends a file with associated metadata to the API to be upserted

    """
    bearerToken = os.environ['BEARER_TOKEN']
    # URL for the request
    url = 'http://0.0.0.0:8000/upsert-file'

    # Headers for the request
    headers = {'Authorization': f'Bearer {bearerToken}'}

    # Round time to nearest second and add to metadata
    # (Note this is actually the last modified time for files)
    created_time = int(time.time() if use_current_time else os.path.getmtime(file_path))
    created_time_string = datetime.datetime.fromtimestamp(created_time).isoformat()
    metadata["created_at"] = created_time_string

    # Add file name as source ID. Note this is not necesarrily unique
    file_name = os.path.basename(file_path)
    metadata["source_id"] = file_name

    # Metadata needs to formated like this to be read by the api model
    metadata_formated = {'metadata': json.dumps(metadata)}

    with open(file_path, 'rb') as f:
        # TODO: Assumes word document
        file = {'file': (file_name, f, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document')}

        # Send the POST request, or print that it would be sent in test run mode
        if not test_run:
            response = requests.post(url, headers=headers, files=file, data=metadata_formated)
            # Print the response
            print(response)
            print(response.text)
        else:
            print("Executing in test run mode. Commands will not be called")
            print(f"URL: {url}")
            print(f"Headers: {headers}")
            print(f"File: {file}")
            print(f"Data: {metadata}")
            print()


# Instantiate the parser
parser = argparse.ArgumentParser(description='Allows a user to manually (via the command line) add documents to the vector database.')

# Add the arguments
parser.add_argument('input', type=str, help='Input file path. If a folder, all files in the folder will be uploaded. If a file, only that file.')
parser.add_argument('--use-current-time', action='store_true', help="If set, created_at time in the post request will be the current time. Otherwise, it will be the file's created date.")
parser.add_argument('--test-run', action='store_true', help="If set, all commands will be printed, but not run.")
parser.add_argument('--author', type=str, help="Sets the author to be included in the metadata of the upsert file post request.")
parser.add_argument('--source', type=str, default="file", help="Sets the file source to be included in the metadata of the upsert file post request.")

# Parse the arguments
args = parser.parse_args()

# Access the values with args.input, args.use_current_time, args.test_run, args.author, args.source
input_path = args.input
use_current_time = args.use_current_time
test_run = args.test_run
author = args.author
source = args.source

metadata = {}

if author:
    metadata["author"] = author

if source:
    metadata["source"] = source

if os.path.isfile(input_path):
    send_upsert_request(input_path, metadata, use_current_time=use_current_time, test_run=test_run)
elif os.path.isdir(input_path):
    # Loop over all files in the folder and upload them
    for file in os.listdir(input_path):
        file_path = os.path.join(input_path, file)

        if os.path.isfile(file_path):
            send_upsert_request(file_path, metadata, use_current_time=use_current_time, test_run=test_run)
        else:
            print(f"Skipping {file}, it is a directory")

else:
    print(f"Could not find file path: {input_path}")
