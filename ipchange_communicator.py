#!/usr/bin/env python
import io
import sys

ip = str(sys.argv[1])

with open('sip-communicator/sip-communicator.xml','r') as file:
	data = file.readlines()

data[34] = '<REGISTRAR_ADDRESS value="' + ip + '"/>\n'
data[43] = '<DEFAULT_DOMAIN_NAME value="' + ip + ':4000"/>\n' 
data[44] = '<DEFAULT_AUTHENTICATION_REALM value="' + ip + ':4000"/>\n'
data[119] = '<OUTBOUND_PROXY value="' + ip + ':4000/udp"/>\n'

with open('sip-communicator/sip-communicator.xml', 'w') as file:
	file.writelines( data )

