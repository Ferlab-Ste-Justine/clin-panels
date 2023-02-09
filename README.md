# clin-panels

Quick tool to compute the panels file while insuring the integrity of symbol/panels unicity.

## Deploy

`deploy.sh` can be used to deploy on S3 the result file ex:

```shell
sh ./deploy.sh panels_20230203.tsv [qa|staging|prod]
```
Second arg **is optional** and `qa` by default

The logic of the `deploy` script is to copy the panel file itself, in order to keep an history of changes, and another copy named `panels.tsv` which is the one used by the ETLs.

*Note: only the file name is required and should be located in /data/output folder.*