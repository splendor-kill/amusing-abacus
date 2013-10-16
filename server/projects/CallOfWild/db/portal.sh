#!/bin/bash

mysql -u$1 -p -hlocalhost  < ./portal.sql
