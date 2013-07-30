#!/bin/bash

mysql -u$1 -p -hlocalhost  < ./gm_account.sql
