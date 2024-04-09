# gamification-adhd-api

## How to run local

Docker:
```
docker run -d -p 1521:1521 -p 5500:5500 -e ORACLE_PDB=orcl -e ORACLE_SID=ORCLCDB -e ORACLE_PWD=oracle -e ORACLE_PDB=orclpdb1 -e ORACLE_MEM=4000 -v /opt/oracle/oradata --name game_database_oracle -e ORACLE_DISABLE_ASYNCH_IO=true -e ORACLE_ALLOW_REMOTE=true virag/oracle-19.3.0-ee-arm64:latest

# inside container
sqlplus system/oracle@ORCLPDB1

alter session set "_ORACLE_SCRIPT"=true;
CREATE USER adhd_game_api IDENTIFIED BY adhd_game_api;
GRANT ALL PRIVILEGES TO adhd_game_api;

ALTER USER system IDENTIFIED BY oracle;
ALTER PROFILE DEFAULT LIMIT PASSWORD_LIFE_TIME UNLIMITED;
```
