#!/bin/bash

echo "Dumping PostgreSQL database 'poc'..."
pg_dump --schema-only --no-owner poc > db_schema.sql

