#HOW TO USE

1. Go to your https://console.cloud.google.com/
2. On Compute Engine, Create New Instance and allow http request
3. Create a firewall rules on "VPC Network -> Firewall Rules"
<p align="center">
<img src="https://user-images.githubusercontent.com/69615570/120776862-dfee7600-c54e-11eb-840b-8e2bd7141f3f.png" widht="332" height="454.67">
  <p align="center">
  Please set the PRIORITY LEVEL to "0" because of some reason

4. Open SSH from your VM Instance on Compute Engine
5. On SSH run:
* "sudo apt update && upgrade"
* "sudo apt install python3.7, git, python-pip"
* "update-alternatives --install /usr/bin/python python /usr/bin/python3 1"
* "pip3 install --upgrade pip"
* "pip3 install Flask, Pillow, numpy, opencv-python"
* "cd ~/B21-CAP0388/Backend/static/"
* "nano index.js"

6. edit the url, replace the localhost to your external ip from GCE
7. download the weight files from https://drive.google.com/uc?export=download&id=1-3jWwLJoYnjQ3Nte9ikJmgv0iwJOCaIU
8. Upload the weight files using SSH "built-in upload features" and choose the weight from your local computer
9. On SSH run:
* "mv ~/yolov4-custom_best.weights ~/B21-CAP0388/backend"
10. EXIT SSH
11. On Cloud Shell TERMINAL:
* "gcloud compute ssh --zone={VM INSTANCE ZONE} {VM INSTANCE NAME} --command="cd ~/B21-CAP0388/Backend && nohup python app.py &" --> using 'nohup' command and '&' from bash for running on the background so you can running without the server being interupted.

12. Go through your http://YOUR-GCE-EXTERNAL-IP-ADDRESS:5000 (ON DEFAULT IT WILL RETURN INTERNAL NOT EXTERNAL)
13. Choose files and sent the response.
14. If nothing happen after sending a HTTP Method to the web server, it means that it doesn't get detected OR it might be have an error response such as server error response. 

Please Check:
(https://developer.mozilla.org/en-US/docs/Web/HTTP/Status#server_error_responses)
