#!/usr/bin/env python3

from flask import Flask, request, jsonify, make_response, url_for, redirect
import subprocess
import random
app = Flask(__name__)

cacrt = "/etc/ssl/ca/ca.crt"
cakey = "/etc/ssl/ca/ca.key"
opensslcmd = ['openssl', 'ca', '-keyfile', cakey, '-cert', cacrt,
              '-extensions', 'usr_cert', '-notext', '-md', 'sha256', '-spkac',
              '/dev/stdin', '-out', '/dev/stdout']

certAttributes = {
    "countryName": "US",
    "stateOrProvinceName": "Washington",
    "localityName": "Bothell",
    "organizationName": "JustChat Enterprises",
    "0.OU": "whut",
    "CN": "lel"
}

removedchars = [
    "\n",
    "\r",
    "\0",
    "\x0B"
]

# Headers
ssl_client_verify = "Client-Verify"
ssl_client_cert = "Client-Certificate"
ssl_client_fingerprint = "Client-Certificate-fp"
ssl_client_serial = "Client-Serial"
ssl_client_s_dn = "Client-S-DN"
ssl_client_i_dn = "Client-I-DN"
ssl_cipher = "SSL-Cipher"
forwarded_for = "X-Forwarded-For"


@app.route("/headers")
def headers():
    return jsonify(request.headers)


@app.route("/")
def index():
    if ssl_client_verify in request.headers:
        if request.headers[ssl_client_verify] == "NONE":
            return """
            Please install the JustChap app to use this service. To play with
            client-SSL certificates, start by <a href="/keygen">generating a key
            """
        if request.headers[ssl_client_verify] == "SUCCESS":
            return redirect(url_for('headers', _scheme="https", _external=True))
        else:
            return "WTF? Your cert validation was a %s" % request.headers[ssl_client_verify]
    else:
        return "HAXXXX (%s header not found!)" % ssl_client_verify


@app.route("/keygen")
def keygen():
    genhtml = """
    Create your ID:
    <form action="/keysign" method="post">
        <label for="CN">Name:</label> <input type="text" name="CN" /><br />
        Cert: <keygen name="pubkey" challange="stuff" keytype="RSA"><br />
        <input type="submit" value="Go" />
    </form><br /><br />

    <i>After your certificate is installed, <a href="/">click here</a></i>
    """
    if ssl_client_verify in request.headers:
        if request.headers[ssl_client_verify] == "SUCCESS":
            return redirect(url_for('headers', _scheme="https", _external=True))
        else:
            return genhtml
    else:
        return "hax"


@app.route("/keysign", methods=["POST"])
def keysign():
    response = make_response("Whoops, no pubkey specified!")
    if ssl_client_verify in request.headers:
        if "pubkey" in request.form and request.headers[ssl_client_verify] == "NONE":
            pubkey = "SPKAC="
            spkac = request.form['pubkey']
            for char in removedchars:
                spkac = spkac.replace(char, "")
            pubkey += spkac

            certAttributes['CN'] = random.randint(0, 1000)
            if "CN" in request.form:
                certAttributes['CN'] = request.form['CN']

            for attribute in certAttributes:
                pubkey += "\n%s=%s" % (attribute, certAttributes[attribute])
            try:
                openssl = subprocess.check_output(opensslcmd,
                                                  input=pubkey.encode('utf-8'))
                response = make_response(openssl)
                response.headers['Content-Type'] = "application/x-x509-user-cert"
            except subprocess.CalledProcessError:
                response = jsonify({
                    "result": "OpenSSL command failed",
                    "command": opensslcmd,
                    "stdin": pubkey
                })
        else:
            return "You silly goose, you already have a certificate! You can't make another one"
    return response

if __name__ == "__main__":
    app.run(debug=True)
