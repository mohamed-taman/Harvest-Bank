#!/usr/bin/env bash
## author: Mohamed Taman
## version: v0.7

echo -e "\nInstalling all Harvest Bank core shared modules & Parent POMs"
echo -e  "...............................................................\n"
echo "1- Installing [Parent Build Chassis] module..."
./mvnw --quiet clean install -U -pl bank-base/bank-build-chassis || exit 126
echo -e "Done successfully.\n"
echo "2- Installing shared [Services Utilities] module..."
./mvnw --quiet clean install -pl bank-common || exit 126
echo -e "Done successfully.\n"
echo "3- Installing [Parent Services Chassis] module..."
./mvnw --quiet clean install -U -pl bank-base/bank-services-chassis || exit 126
echo -e "Done successfully.\n"

echo -e "Woohoo, building & installing all project modules are finished successfully.\n\
The project is ready for the next step. :)"