# ChatGPT Retrieval Plugin

This repository stores my experiments with OpenAI's [ChatGPT Retrieval Plugin](https://github.com/openai/chatgpt-retrieval-plugin). My goal is to upload all the cover letters I've written for my job applications, and by giving ChatGPT access to my previous writings I will be able to more quickly iterate on future letters.

## Setup & Useful Commands

1. Pull a docker container with Postgres and PGVector preinstalled: `docker pull ankane/pgvector`
2. Startup a new contianer: `docker run -itd --name pgvector -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password -p 5432:5432 ankane/pgvector`
3. Install PostgreSQL CLI: `sudo apt install postgresql`
4. Configure new database for use with the plugin: `psql -h localhost -p 5432 -U postgres -d postgres -f examples/providers/supabase/migrations/20230414142107_init_pg_vector.sql`
5. Load environment variables: `set -a; source .env; set +a`
6. Run tests (warning: may interrupt data you already have): `poetry run pytest -s ./tests/datastore/providers/postgres/test_postgres_datastore.py`
7. Run the API locally: `poetry run start`
8. Acces the API locally at `http://0.0.0.0:8000/docs`. Make sure to add your bearer token in the webpage.

### Miscellaneous Commands

* Check which processes are using a port, in case the port is already in use: `sudo lsof -i tcp:5432`
