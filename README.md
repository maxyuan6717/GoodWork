# Good Work Application

Why waste energy alone in the gym when you could be using that energy to both positively influence those around you and improve your health.

Good Work is an android application that crawls volunteermatch.org for volunteer opportunities around you and uses machine learning to determine which opportunities should be pushed to the user

The data that is crawled from the website is first uploaded to a Firebase realtime database. The android app can then access this data and display it to the user. The "10004.txt" file is the training data for the text classification routine within the crawler code, which uses linear regression to predict whether or not a certain volunteer opportunity requires any physical exertion.
