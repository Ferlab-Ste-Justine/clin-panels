# clin-panels

Quick tool to compute the panels file while insuring the integrity of symbol/panels unicity. It has two modes:

- **import** will load an Excel file from S3 and generate the panels file from it. Can be used from *airflow*
- **manual (deprecated)** where the user writes how to build the panels file from inputs 


## Import (from Airflow)

How to run local docker image to validate:

```
docker-compose up
docker build -t clin-panels
docker run --network host clin-panels org.clin.panels.command.Import -f=panels_RQDM_20230221T210806Z.xlsx
```

**Note: panels_RQDM_20230221T210806Z.xlsx should be available in s3://cqdg-qa-app-public/panels**

## Manual (deprecated)

Update the `Main.java` main method with the logic you want to implement and then use `deploy.sh` to deploy on S3 the result file ex:

```shell
sh ./deploy.sh panels_20230203.tsv [qa|staging|prod]
```
Second arg **is optional** and is `qa` by default

The logic of the `deploy` script is to copy the panel file itself, in order to keep an history of changes, and another copy named `panels.tsv` which is the one used by the ETLs.

*Note: only the file name is required and should be located in /data/output folder.*