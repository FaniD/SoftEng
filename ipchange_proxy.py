#!/usr/bin/env python
import io
import sys

ip = str(sys.argv[1])

with open('sip-proxy/src/gov/nist/sip/proxy/configuration/configuration.xml','r') as file:
	data = file.readlines()

data[4] = 'stack_IP_address="' + ip + '"\n'

with open('sip-proxy/src/gov/nist/sip/proxy/configuration/configuration.xml', 'w') as file:
	file.writelines( data )

