import os

image_files = []
os.chdir(os.path.join("/content/drive/MyDrive/Capstone_Project/Colab/YOLOV4-Pothole/workspace/dataset", "/content/drive/MyDrive/Capstone_Project/Colab/YOLOV4-Pothole/workspace/dataset/test"))
for filename in os.listdir(os.getcwd()):
    if filename.endswith(".jpg"):
        image_files.append("/content/drive/MyDrive/Capstone_Project/Colab/YOLOV4-Pothole/workspace/dataset/test/" + filename)
os.chdir("..")
with open("test.txt", "w") as outfile:
    for image in image_files:
        outfile.write(image)
        outfile.write("\n")
    outfile.close()
os.chdir("..")
