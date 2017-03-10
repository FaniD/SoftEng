# README #

VoIP application using SIP protocol.

Some details about our work:
* User logins automatically after sign up.
* User is supposed to know the contacts who has friends, business, forward and block.
* Up to 5 friends. Then the user must unfriend some of his friends to add a new one. Same with business (up to 2 business) and forward (up to 1). 
* If user is not online forwarding is not taking place.

### How do I get set up? ###

1. Run python scripts to fix the ip for proxy and communicator:
python ipchange_proxy.py <proxys_ipx>
python ipchange_communicator.py <communicator_ip>

2. Open the project in Eclipse. Import sip-proxy and sip-communicator

3. Run sip proxy: Right click on gov.nist.sip.proxy.gui.ProxyLauncher, Run As -> Run configurations -> New launch configuration at Java Application -> Arguments -> <check arguments.txt>
Run sip communicator: Right click on net.java.sip.communicator.SipCommunicator.java, Run As -> Run configurations -> New launch configuration at Java Application -> Arguments VM -> <check arguments.txt>

Ready to use

### Contribution guidelines ###

* fix authentication bugs