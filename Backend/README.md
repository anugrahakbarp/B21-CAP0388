#HOW TO USE

1. Go to your console.cloud.google.com
2. On Compute Engine, Create New Instance and allow http request
3. Open SSH from your VM Instance on Compute Engine
4. On SSH run:
* "sudo apt update && upgrade"
* "sudo apt install python3.7, git, python-pip, wget"
* "wget --no-check-certificate 'https://drive.google.com/uc?export=download&id=1-3jWwLJoYnjQ3Nte9ikJmgv0iwJOCaIU' -O yolov4-custom_best.weights"
* "update-alternatives --install /usr/bin/python python /usr/bin/python3 1"
* "pip3 install --upgrade pip"
* "pip3 install Flask, Pillow, numpy, opencv-python"
* "python app.py"
5. Go through your http://YOUR-GCE-INTERNAL-IP-ADDRESS:5000
