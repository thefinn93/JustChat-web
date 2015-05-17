#!/bin/bash

########################## post-merge hook ############################
# This script gets executed by the server every time you push changes.#
# It's pretty cool, and but also lets you fuck shit up pretty good,   #
# So be aware of that before you change shit. Also all the non-ant    #
# stuff is automation for other things, so don't touch that.          #
#######################################################################

# Ensure that the bot is in the channel on freenode.
echo "/j #justchat" > /home/admin/spambot/chat.freenode.net/in

# Run everything out of the JavaServer directory.
cd /usr/local/src/JustChat-web/JavaServer


ant -f netbeanless.xml 2>&1 | curl -F 'sprunge=<-' http://sprunge.us > /tmp/channelspam

# Finally, restart the server so the changes get executed
supervisorctl restart javaserver
