####Questions about the implementation of Fusion

### Support multiple value streams

- Should we enable multiple valued stream? For example a multiple valued stream is 
a sensor id(String), the sensor value(int,double,float,long), timestamp(long) etc.
If we support this the spout will have to know the class values(String, int/double/float/long, long)
in order to split the message of the queue to the corresponding values.
Furthermore we have to inform the following bolts of the values map that we send into the queues
(possible solution -> config?)
Lastly bolt algorithms will have to adapt to this scenario, meaning that we might have to supply extra info.
e.g. Max algorithm should know which value he is going to monitor(2nd value of our example)

### Support split to multiple streams
- We have to support a clever way so that the user can split a stream generally.
- A spout that wants to split n-ways can do so without any condition. Εννοούμε οτι σε ενα spout 
αν ο χρήστης θέλει να φτιάξει ν-ροές μπορεί να το κάνει απλά επιλέγοντας να κάνει publish σε ν-named streams.
Στην περίπτωση αυτή δεν υπάρχει λόγος ύπαρξης κάποιας συνθήκης μιας και απλά θέλουμε να κλωνοποίσουμε το stream

- Η περίπτωση αυτή διαφέρει στα Bolts μιας και στα bolts υπάρχει κάποια συνθήκη η οποία διαφοροποιεί που θα
προωθήσουμε τις τιμες. Πχ στην περίπτωση του threshold υπάρχει μια συνθήκη διαφοροποίησης που προφανως δημιουργεί δυο streams
αναγκαστικά, αυτά που περνάνε το κατώφλι και αυτά που απορρίπτονται.

- Αλγόριθμοί όπως μαξ και μιν πρέπει ίσως να έχουν κάποιο κοινό σημείο αναφοράς; πχ αν είναι κατανεμημένη η τοπολογία δεν θα έχει κάποιο cache 
στο οποίο θα μαζευτούν τα αποτελέσματα στο τέλος;
