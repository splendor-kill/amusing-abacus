#!/bin/bash

mysql -u$1 -p -hlocalhost  < ./db.sql
