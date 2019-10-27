import requests
import math
import re
import time
import pandas as pd
import numpy as np

from bs4 import BeautifulSoup
from firebase import firebase
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.linear_model import LogisticRegression

#-------------------------------------------------------------------------------------------------

testing = False # Boolean for whether or not I am testing just first 10 opportunities

#-------------------------------------------------------------------------------------------------

database_url = 'https://workoutforgood-917a6.firebaseio.com/'

frb = firebase.FirebaseApplication(database_url,None)

while (testing == False and frb.get('/Update/bool',None) != 'True'): # Fetch update variable
    garbage = 0 # Useless statement

#-------------------------------------------------------------------------------------------------

start_time = time.time()

"""
result = frb.post('/TestData/',data_to_upload) # Add Data to database
print(result)
frb.delete('/TestData/', None) # Delete Data to database
"""

#frb.delete('/Opportunities',None)

frb.delete('/Vol_Opp',None) # Reset lists in database

base_url = "https://www.volunteermatch.org"
url = "https://www.volunteermatch.org/search" # VOLUNTEER WEBSITE URL

# zipcode = "06520" # NEW HAVEN ZIP CODE
zipcode = frb.get('/User/','ZipCode') # Fetch zipcode from database
print("ZIP Code: " + str(zipcode))

homePage = requests.get(url, params={'l': zipcode, 'categories':{13,14,19,42}}) # Web page to retrieve number of opportunities
#homePage = requests.get(url, params={'l': zipcode}) # Web page to retrieve number of opportunities
if homePage.status_code != 200: # Make sure webpage is valid
    raise Exception('Unsuccessful response: code {}'.format(homePage.status))
soup = BeautifulSoup(homePage.content, 'lxml')
opp_cnt = int(soup.find(class_="search_results_size").string) # Find webpage count

upload_false = {
    'bool': 'False'
}

if (opp_cnt == 0):
    res = frb.patch('Update',upload_false)
    print("NO OPPORTUNITIES")
    quit()

upload_cnt = {
    'Opp_Cnt': opp_cnt
}

frb.patch('Vol_Opp',upload_cnt)

print("Number of Opportunities: " + str(opp_cnt))

#-------------------------------------------------------------------------------------------------

# MACHINE LEARNING PORTION

filepath_dict = { # Where training data is stored
    '10004': '10004.txt'
}

df_list = []
for source, filepath in filepath_dict.items():
    df = pd.read_csv(filepath, names=['sentence', 'label'], sep='\t')
    df['source'] = source  # Add another column filled with the source name
    for x in range(len(df['label'])):
        if (np.isnan(df['label'][x])):
            df['label'][x] = 0
    df_list.append(df)
df = pd.concat(df_list)

df_nyc = df[df['source'] == '10004']

sentences = df_nyc['sentence'].values
Y = df_nyc['label'].values

sentences_train, sentences_test, Y_train, Y_test = train_test_split(
   sentences, Y, test_size= 0.99, random_state=1000)

vectorizer = CountVectorizer()
vectorizer.fit(sentences_train)

X_train = vectorizer.transform(sentences_train)

classifier = LogisticRegression(solver='lbfgs')
classifier.fit(X_train, Y_train)

#-------------------------------------------------------------------------------------------------

pageCnt = int(math.ceil(opp_cnt/10.0)) # Number of pages
oppCur = 1 # Current Opportunity Count
opp_names = [] # List of opportunity names
opp_org = [] # List of opportunity organizations
opp_ratings = [] # List of opportunity ratings
opp_urls = [] # List of opportunity urls
opp_info = [] # List of opportunity descriptions

#-------------------------------------------------------------------------------------------------

for x in range(pageCnt): # Loop through each page
    page = requests.get(url,params={'s': oppCur, 'l': zipcode, 'categories':{13,14,19,42}}) # Retrieves HTML of page
    #page = requests.get(url,params={'s': oppCur, 'l': zipcode}) # Retrieves HTML of page
    if page.status_code != 200:
        raise Exception('Unsuccessful response: code{}'.format(page.status))
    soup = BeautifulSoup(page.content, 'lxml')
    opportunities = soup.find_all(class_="searchitem PUBLIC") # Finds all opportunities

    names = [] # List of opp and org names
    reviews = [] # List of stars
    for x in opportunities:
        names.append(x.find_all(class_="psr_link"))
        rating = x.find(class_="stars")
        if (rating == None): # No ratings
            reviews.append(-1)
        else: # Has ratings
            reviews.append(rating)

    for x in names:
        opp_names.append(x[0].string) # Add name to list
        #opp_org.append(x[1].string) # Add organization to list
        opp_urls.append(base_url + x[0]['href']) # Add url to list

        more_info = requests.get(opp_urls[-1]) # Get html of more info page
        page2 = BeautifulSoup(more_info.content,'lxml') # Make BeautifulSoup object out of that
        strings = [] # List to hold strings of description
        description = page2.find(id="short_desc") # Tag with the description

        opp_org_name = page2.find(class_='link-body-text text-sm caps') # Find full organization name
        if (opp_org_name == None):
            opp_org.append("N/A")
        else:
            opp_org.append(opp_org_name.string) # Add organization to List

        if(description != None):
            for string in description.stripped_strings:
                strings.append(string) # Add strings to List

        full_info = ' '.join(strings) # Combine list into just one string
        opp_info.append(full_info) # Add string to List

        #print(full_info + "\n\n")

        #print("Opportunity #" + str(oppCur).ljust(3) + ": " + opp_names[-1].ljust(90) + " | " + opp_org[-1])
        oppCur = oppCur + 1 # Increment current opportunity count
        upload_curOpp = {
            'oppCur':oppCur-1
        }
        #frb.delete('/CurCnt',None)
        res = frb.patch('CurCnt',upload_curOpp)

    for x in reviews:
        if (x == -1): # No rating
            opp_ratings.append(-1)
            continue
        stars = 0.0
        for s in x.contents: # Loop over all star images
            tag_str = str(s)
            if ("full_star" in tag_str): # Full star
                stars = stars+1.0
            elif ("half_star" in tag_str): # Half star
                stars = stars+0.5
        opp_ratings.append(stars) # Add star count to list


    if testing: # Only run 10 cases
        break

#-------------------------------------------------------------------------------------------------

X_test = vectorizer.transform(opp_info) # Create test data
Y_test = classifier.predict(X_test) # Use machine learning to predict which opportunities are physical activities

tempCur = 1 # Current Opportunity Count
temp_names = [] # List of opportunity names
temp_org = [] # List of opportunity organizations
temp_ratings = [] # List of opportunity ratings
temp_urls = [] # List of opportunity urls
temp_info = [] # List of opportunity descriptions
indx = 0

for x in Y_test:
    if (x == 0): # If machine learning algorithm predicted no...
        indx = indx + 1
        continue
    temp_names.append(opp_names[indx])
    temp_org.append(opp_org[indx])
    temp_ratings.append(opp_ratings[indx])
    temp_urls.append(opp_urls[indx])
    temp_info.append(opp_info[indx])
    indx = indx + 1
    tempCur = tempCur + 1

#-------------------------------------------------------------------------------------------------

opp_cnt = tempCur-1
opp_names = temp_names
opp_org = temp_org
opp_ratings = temp_ratings
opp_urls = temp_urls
opp_info = temp_info

lists_to_upload = { # Data to upload to database
    'Opp_Cnt': opp_cnt,
    'Opp_Names': opp_names,
    'Opp_Orgs': opp_org,
    'Opp_Ratings': opp_ratings,
    'Opp_Urls': opp_urls,
    'Opp_Info': opp_info
}

upload_false = {
    'bool': 'False'
}

#res = frb.post('/Volunteer_Opportunities/',lists_to_upload) #upload
#frb.delete('/Vol_Opp',None) # Reset lists in database
res = frb.patch('Vol_Opp',lists_to_upload) # Upload lists to database
res = frb.patch('Update',upload_false)
#print(res) #check status

#-------------------------------------------------------------------------------------------------

for x in range(opp_cnt):
    print("Opportunity #" + str(x+1).ljust(3) + ": " + opp_names[x].ljust(90) + " | " + opp_org[x].ljust(70) + " | " + str(opp_ratings[x]))

#-------------------------------------------------------------------------------------------------

end_time = time.time()
print ("Program Execution Time: " + str(round(end_time-start_time,2)) + " seconds")
