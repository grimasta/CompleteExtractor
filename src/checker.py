import os
import shutil
path = "3000commits/"

for folder in os.listdir(path):
    projectpath = path + folder
    count = 0
    for folder_analysed in os.listdir(projectpath):
        if "dbdump" in os.listdir(projectpath + "/" + folder_analysed + "/dbdump"):
#            print(projectpath + "/" + folder_analysed)
            shutil.rmtree(projectpath + "/" + folder_analysed + "/dbdump/dbdump")
            count += 1
    print(projectpath, count)    

