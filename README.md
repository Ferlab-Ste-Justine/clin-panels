# clin-panels

Quick tool to compute the panels file while insuring the integrity of symbol/panels unicity. It has two modes:

- **import CLI** will load an Excel file from S3 and generate then publish the panels file from it.
- **manual (deprecated)** where the user writes how to build the panels file from inputs


## Import CLI

### Setup

How to run the `Import` command in local:

- Start `docker-compose up` 
- Open your browser to `http://localhost:9000`
- Use `minio` and `minio123` to login
- Add both buckets `cqgc-qa-app-public` and `cqgc-qa-app-datalake` (if missing)
- Upload the input excels panels files into `cqgc-qa-app-public/panels` folder

The local env is now ready to work with. If any issues please check if the default configuration match de one in: `src/main/resources/application.conf` and update if needed.

### Run from IDE

Open you preferred IDE and run the `main` function of `org.clin.panels.command.Import` with the following param `-f or --file` and specify the excel panel file name you whish to import.

*Note: the excel panels file should be available in s3://cqgc-qa-app-public/panels*

### Run from Docker

```
docker build -t clin-panels

docker run --network host clin-panels org.clin.panels.command.Import --file=panels_RQDM_20230221T210806Z.xlsx
```
*Note: the docker build may take some time because it uses `jdeps and jlink` to build a custom JRE*
### Validation

One important part of the `Import` command is the `validation` feature of the new imported panels file and display a summary of changes such as this example:

```
Current model:
- Panels: [RHAB, MITN, MYOC, RGDI+, MMG, MYAC, RGDI, HYPM, POLYM, DYSM]
- Versions: [MMG_v1, MITN_v1, MYOC_v1, RGDI_v2, DYSM_v1, RGDI+_v1, HYPM_v1, POLYM_v1, RHAB_v1, MYAC_v1]
- Symbols: 2518

Previous model:
- Panels: [RHAB, MITN, MYOC, RGDI+, MMG, MYAC, RGDI, HYPM, POLYM, DYSM]
- Versions: [MMG_v1, MITN_v1, MYOC_v1, RGDI_v2, DYSM_v1, RGDI+_v1, HYPM_v1, POLYM_v1, RHAB_v1, MYAC_v1]
- Symbols: 2517

New symbols:
AARS2 => [MITN, POLYM]
AASS => [POLYM, RGDI, RGDI+]

Updated symbols:
AAAS [POLYM, RGDI] => [POLYM, RGDI, RGDI+]
AARS1 [MMG, POLYM, RGDI, RGDI+] => [POLYM, RGDI, RGDI+]

Deleted symbols:
AASSX
```
### Additional params

The tools provides additional params such as `--dryrun` and `-d or --debug`. The first one validate only the excel panels file without publishing on S3 (replacing the previous panels). As for the second param it enables all `DEBUG` logs of the app.

## Manual (deprecated)

Update the `Main.java` main method with the logic you want to implement and then use `deploy.sh` to deploy on S3 the result file ex:

```shell
sh ./deploy.sh panels_20230203.tsv [qa|staging|prod]
```
Second arg **is optional** and is `qa` by default

The logic of the `deploy` script is to copy the panel file itself, in order to keep an history of changes, and another copy named `panels.tsv` which is the one used by the ETLs.

*Note: only the file name is required and should be located in /data/output folder.*