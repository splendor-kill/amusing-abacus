#!/bin/bash


mysql -uroot -p -h192.168.66.59 trickraft << BxxEOFMYSQL
    insert into ServerConfig(PORT, ip, NAME) values(8080, '192.168.66.59', 'ubuntu-game-center'); 
BxxEOFMYSQL

#mysql -uroot -p -P3306 -h192.168.66.59 trickraft -e "insert into ServerConfig(PORT, ip, NAME) values(8080, '192.168.66.59', 'ubuntu-game-center');"

#mysql -uroot -p -h192.168.66.59 trickraft << BLOCKFORTEST
#    update Player set id=57005 where NAME ='zergling';
#BLOCKFORTEST
