from typing import List
import re

import docx2txt

def split_text_into_sentences(text: str) -> List[str]:
    # Split the text based on a period, exclamation mark, question mark, or new line.
    sentences = re.split('[.!?]\s|\n', text)

    # Remove any resulting split that is empty or a single newline character
    # s.strip() returns true if there is text left after removing all whitespace characters
    sentences = [s for s in sentences if s.strip()]

    # Return resulting splits
    return sentences


def extract_text_from_docx_file(filepath: str) -> str:

    with open(filepath, 'rb') as f:
        extracted_text = docx2txt.process(f)

    return extracted_text


def main():
    filepath = "Cover Letters/RightHand Robotics Cover Letter.docx"
    text = extract_text_from_docx_file(filepath)
    sentences = split_text_into_sentences(text)
    for s in sentences:
        print(s)


if __name__ == "__main__":
    main()
