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


@app.route("/")
def hello():
    return "Please install the JustChap app to use this service."


@app.route("/headers")
def headers():
    return jsonify(request.headers)


@app.route("/keygen")
def keygen():
    genhtml = """
    Create your ID:
    <form action="/keysign" method="post">
        <label for="CN">Name:</label> <input type="text" name="CN" /><br />
        Cert: <keygen name="pubkey" challange="stuff" keytype="RSA"><br />
        <input type="submit" value="Go" />
    </form>
    """
    if "X-Client-Verify" in request.headers:
        if request.headers['X-Client-Verify'] == "SUCCESS":
            return redirect(url_for('headers'))
        else:
            return genhtml
    else:
        return "hax"


@app.route("/keysign", methods=["POST"])
def keysign():
    response = make_response("Whoops, no pubkey specified!")
    if "X-Client-Verify" in request.headers:
        if "pubkey" in request.form and request.headers['X-Client-Verify'] == "NONE":
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
