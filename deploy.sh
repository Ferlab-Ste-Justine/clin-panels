#!/bin/sh

FILE=${1}
ENV="${2:-qa}"

# copy + list S3, update profile name if necessary
echo "=== COPY TO S3 ($ENV) ==="
aws --profile cqgc-$ENV --endpoint https://s3.cqgc.hsj.rtss.qc.ca s3 cp ./data/output/$FILE s3://cqgc-$ENV-app-datalake/raw/landing/panels/$FILE
aws --profile cqgc-$ENV --endpoint https://s3.cqgc.hsj.rtss.qc.ca s3 cp ./data/output/$FILE s3://cqgc-$ENV-app-datalake/raw/landing/panels/panels.tsv

echo "=== JARS IN S3 ($ENV) ==="
aws --profile cqgc-$ENV --endpoint https://s3.cqgc.hsj.rtss.qc.ca s3 ls s3://cqgc-$ENV-app-datalake/raw/landing/panels/ --recursive