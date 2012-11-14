#!/bin/bash
############################################
# One script to run it all
# Boots up the entire system at one
############################################
# Identify this as the root directory
root=`pwd`

# Parametrize the variables corresponding to directories of individual modules
LEIA_HOME=$root/Leia
R2D2_HOME=$root/R2D2
C3PO_HOME=$root/C3PO
SIP_SERVER_HOME=$root/SIPServer
HANSOLO_HOME=$root/HanSolo

# Start Leia server to support messaging
cd $LEIA_HOME
xterm -e "ant" &


## Give the program some time to compile
sleep 3

# start Text to speech server
cd $R2D2_HOME
xterm -e "ant" &

## Give the program some time to compile
sleep 3

# start speech recognizer
cd $C3PO_HOME


xterm -e "ant" &

## Give the program some time to compile
sleep 3
# start SIP
cd $SIP_SERVER_HOME
xterm -e "ant" &

## Give the program some time to compile and run
sleep 3

# start Windu
cd $HANSOLO_HOME
xterm -e "ant" &



