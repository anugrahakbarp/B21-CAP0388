#HOW TO USE

1. Go to your console.cloud.google.com
2. On Compute Engine, Create New Instance and allow http request
3. Open SSH from your VM Instance on Compute Engine
4. On SSH run:
* "sudo apt update && upgrade"
* "sudo apt install python3.7, git, python-pip"
* "update-alternatives --install /usr/bin/python python /usr/bin/python3 1"
* "pip3 install --upgrade pip"
* "pip3 install Flask, Pillow, numpy, opencv-python"
* "cd ~/B21-CAP0388/Backend/static/"
* "nano index.js"

5. edit the url, replace the localhost to your external ip from GCE
6. download the weight files from https://drive.google.com/uc?export=download&id=1-3jWwLJoYnjQ3Nte9ikJmgv0iwJOCaIU
7. Upload the weight files using SSH "built-in upload features" and choose the weight from your local computer
8. On SSH run:
* "mv ~/yolov4-custom_best.weights ~/B21-CAP0388/backend"
* "python app.py"

9. Go through your http://YOUR-GCE-EXTERNAL-IP-ADDRESS:5000 (ON DEFAULT IT WILL RETURN INTERNAL NOT EXTERNAL)
10. Choose files and sent the response.
11. If nothing happen sent the response to web server, it means that it doesn't get detected OR it might be have an error response such as server error response.

Check --> (https://developer.mozilla.org/en-US/docs/Web/HTTP/Status#client_error_responses)
