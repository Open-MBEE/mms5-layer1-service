#!/bin/bash
N_MMS5_BG_PORT=8081
S_MMS5_BG_HOST=localhost
P_MMS5_BG_REST="http://$S_MMS5_BG_HOST:$N_MMS5_BG_PORT/bigdata"
P_MMS5_BLAZEGRAPH_PROPERTIES_FILE="mms5-blazegraph.properties"
XTL_TIMEOUT=15

docker run --network=host --name mms5-store -d \
	-p "$N_MMS5_BG_PORT:8080" \
	-v "$PWD/$P_MMS5_BLAZEGRAPH_PROPERTIES_FILE:/$P_MMS5_BLAZEGRAPH_PROPERTIES_FILE" \
	-v "$PWD/data:/data" \
	lyrasis/blazegraph:2.1.5

