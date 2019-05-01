#!/bin/bash
mkdir -p /usr/local/WowzaStreamingEngine/applications/vLive

cd /usr/local/WowzaStreamingEngine/conf

if [[ -n "$MY_POD_NAME" ]]; then
    EC2_REGION="`echo $MY_NODE_NAME | cut -d'.' -f2`"
    SERVER_NODE_TAG="${MY_POD_NAME}.${EC2_REGION}"
    echo "setting SERVER_NODE_TAG $SERVER_NODE_TAG"
fi

if [[ $(command -v nvidia-smi >> /dev/null && nvidia-smi -L | grep -i gpu) ]]; then
    export GPU_SUPPORT="true"
    echo "Setting GPU_SUPPORT as $GPU_SUPPORT"
fi

if [ -z "$DISABLE_SERVER_NODE_CONF_UPDATE" ]; then
        source /sbin/updateServerNodeConfiguration.sh
fi

# replace config
sed -e "s#@VIDIUN_SERVICE_URL@#$SERVICE_URL#g" \
    -e "s#@VIDIUN_PARTNER_ID@#$PARTNER_ID#g" \
    -e "s#@VIDIUN_PARTNER_ADMIN_SECRET@#$PARTNER_ADMIN_SECRET#g"\
    -e "s#@HOST_NAME@#$SERVER_NODE_HOST_NAME#g"\
     Server.xml.template > Server.xml

sed -e "s#@VIDIUN_SERVICE_URL@#$SERVICE_URL#g" \
    -e "s#@VIDIUN_PARTNER_ID@#$PARTNER_ID#g" \
    -e "s#streamName#partnerId/$PARTNER_ID/streamName#g" \
    ./vLive/Application.xml.template > ./vLive/Application.xml

exec /sbin/entrypoint.sh
