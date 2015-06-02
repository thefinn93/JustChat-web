#!/usr/bin/env python
from flask import Flask, request, jsonify, make_response
import subprocess
import random
from raven.contrib.flask import Sentry
app = Flask(__name__)

# Sentry settings
dsn = "https://9b87b67e2eb64cfe8b1e25b6fe91622c:481dfdff04fb423d9ea84e878de4945a@sentry.finn.io/4"


# Headers
ssl_client_verify = "Client-Verify"
ssl_client_cert = "Client-Certificate"
ssl_client_fingerprint = "Client-Certificate-fp"
ssl_client_serial = "Client-Serial"
ssl_client_s_dn = "Client-S-DN"
ssl_client_i_dn = "Client-I-DN"
ssl_cipher = "SSL-Cipher"
forwarded_for = "X-Forwarded-For"


# Raven Client
app.config['SENTRY_DSN'] = dsn
sentry = Sentry(app)

cacrt = "/etc/ssl/ca/ca.crt"
cakey = "/etc/ssl/ca/ca.key"

subject = '/countryName=US/stateOrProvinceName=Washington/localityName=Bothell'
subject += '/organizationName=JustChat Enterprises/commonName='

opensslcmd = ['openssl', 'ca', '-keyfile', cakey, '-cert', cacrt, '-extensions', 'usr_cert',
              '-notext', '-md', 'sha256', '-in', '/dev/stdin', '-out', '/dev/stdout', '-batch',
              '-subj', subject]

removedchars = [
    "\n",
    "\r",
    "\0",
    "\x0B"
]

certAttributes = {
    "countryName": "US",
    "stateOrProvinceName": "Washington",
    "localityName": "Bothell",
    "organizationName": "JustChat Enterprises",
    "0.OU": "whut",
    "CN": "lel"
}


@app.route("/keysign", methods=["POST"])
def keysign():
    response = {
        "success": False,
        "reason": "Whoops, no pubkey specified!"
    }
    if ssl_client_verify in request.headers:
        body = request.get_json(force=True)
        SENTRY_REQUEST_BODY = body
        if "csr" in body and "CN" in body and request.headers[ssl_client_verify] == "NONE":
            csr = body['csr']

            try:
                cmd = opensslcmd
                cmd[-1] += body['CN']
                openssl = subprocess.check_output(cmd, input=csr.encode('utf-8'))
                response['cert'] = openssl
                response['success'] = True
                response['CN'] = certAttributes['CN']
            except subprocess.CalledProcessError:
                response = {
                    "reason": "OpenSSL command failed",
                    "command": opensslcmd,
                    "stdin": csr,
                    "success": False
                }
        else:
            sentry.captureMessage("Got a keysign request with a client cert or invalid body")
            response['reason'] = "You silly goose, you already have a certificate!"
    return jsonify(response)

#
# @app.route('/sign')
# def signCSR():
#     response = {
#         "result": "failure",
#         "reason": "Unknown"
#     }
#     try:
#         if ssl_client_verify in request.headers:
#             if "csr" in request.form and request.headers[ssl_client_verify] == "SUCCESS":
#                 with open(caInfoFile, 'w') as caInfo:
#                     csr = crypto.load_certificate_request(crypto.TYPE_RSA, request.form['csr'])
#
#                     info = json.load(caInfo)
#                     certinfo = {
#                         "serial": info['certs'][-1]['serial'] + 1,
#                         "cn": csr.getSubject()
#                     }
#
#                     cert = crypto.X509()
#                     cert.set_serial_number(certinfo['serial'])
#                     cert.gmtime_adj_notBefore(0)
#                     cert.gmtime_adj_notAfter(60*60*24*365*5)
#                     cert.set_issuer(issuer)
#                     cert.set_subject(csr.getSubject())
#                     cert.set_pubkey(csr.get_pubkey(), "sha256")
#                     cert.sign(ca.get_privatekey(), "sha256")
#     except crypto.Error as e:
#         sentry.captureException()
#         response['reason'] = "Exception"
#         response['exception'] = str(e)
#
#     return jsonify(response)


if __name__ == "__main__":
    app.run(debug=True)
