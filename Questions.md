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


# Γενικές παρατηρήσεις

### Περιγραφή προβλημάτων:
#### Προβλημα ροών
Το Storm προσφέρει ενα γενικό επίπεδο αφαίρεσης για την διαχείρηση ροών. Υπάρχουν δύο βασικά συστατικά του framework που προσφέρει το Storm: το Spout και το Bolt.
Καθε Spout είναι υπευθυνο για την παραγωγή/προώθηση στοιχείων της ροής προς το σύστημα. Επομένως ενα spout μπορεί να προσφέρει μηνύματα τα οποία καταναλώνει απο μια ουρά, 
απο ένα αρχείο το οποίο ανοιγει και διαβάζει και γενικά είναι υπεύθυνο για την προώθηση των δεδομένων στο σύστημα.
Το επόμενο στοιχείο του Storm, το Bolt είναι υπεύθυνο για την εφαρμογή κάποιας μορφής υπολογισμού πανω απο τα δεδομένα. Ενδεχομένως να χρειάζεται να υπολογίζουμε την μέση τιμή
μιας τιμής που εμφανίζεται στην ροή κτλ. 
Βασική λογική του Storm είναι η δημιουργία ενός γράφου απο συνδεδεμένα bolts τα οποία εφαρμόζουν υπολογισμούς πάνω απο τα δεδομένα της ροής. Προφανώς το αρχικό συστατικό θα είναι
ενας αριθμός απο spouts που θα προσφέρουν τις ροές δεδομένων.
Ο τρόπος με τον οποίο υποστηρίζεται η δημιουργία της αλληλουχίας των bolts είναι μέσω του framework που προσφέρει το Storm.
Για παράδειγμα:

```     
    TopologyBuilder builder = new TopologyBuilder();
    builder.setSpout("uuid-spout", new UUIDSpout());
    builder.setBolt("counter", new CountBolt()).shuffleGrouping("uuid-spout");
    LocalCluster cluster = new LocalCluster();
    cluster.submitTopology("test",cnf , builder.createTopology());
```
Εδω περιγράφουμε μια τοπολογία στην οποία ενα Spout παράγει UUIDs τα οποία
στέλνονται σε ενα Bolt τα οποία μετράει τα UUIDs. 
Ο τρόπος με τον οποίο κάνουμε την σύνδεση του bolt με το spout είναι μέσω της γραμμής 
```
    builder.setBolt("counter", new CountBolt()).shuffleGrouping("uuid"); 
```
Που όπως φαίνεται εδώ γίνεται μέσω του id που έχουμε δώσει για το κάθε component. Στο σημείο αυτό μπορούμε να αυξήσουμε και πόσα threads θα μπορούσαν να τρέξουν το
Bolt αυξάνοντας την παραλληλία. πχ: 

builder.setBolt("counter", new CountBolt(), __2__ ).shuffleGrouping("uuid");

Αυτό σημαίνει οτι θα δημιουργηθούν δύο instances απο τα counter bolts τα οποία θα αναλάβουν να μετρήσουν τα UUIDs και σαν συνέπεια αυτού θα πρέπει στο τέλος να 
συγκεντρωθούν τα αποτελέσματα τους. Αυτό συμβαινει γιατί με τον τύπο shuffle grouping κάθε φορά που είναι να στείλουμε ενα tuple απο ενα bolt η ενα spout 
σε ένα αλλο bolt ή spout επιλέγεται τυχαία το instance του bolt που θα παραλάβει το tuple. Επομένως στην περίπτωση μας ενδέχεται να έχουμε τα δύο instances των 
bolts να έχουν μετρήσει το ίδιο UUID και τα δύο(πχ 2 και 3 φορές) και το άθροισμα τους να είναι το σωστό.
Για να το πετύχουμε αυτό και να συγκεντρωθουν τα αποτελέσματα σε ενα bolt πρέπει να χρησιμοποιήσουμε το FieldsGrouping ετσι ώστε να φροντίσουμε όταν φτάσει ενα συγκεκριμένο
uuid να πάει σε ένα συγκεκριμένο instance ενός bolt και κάθε επόμενο ίδιο uuid να πάει στο ίδιο instance του bolt. Για να γίνει αυτό θα πρέπει να επιλέξουμε ενα ή περισσότερα πεδία
με βάση το οποίο θα γίνει το grouping:
```
    builder.setBolt("counter", new CountBolt(), 2).fieldsGrouping("uuid-spout", new Fields("uuid")); 
```
Εδώ το Field το οποίο χρησιμοποιούμε για να γίνει το grouping είναι μόνο το "uuid" το οποίο θα πρέπει να έχουμε δηλώσει μέσα στο uuid-spout στην μέθοδο 
```
    public void declareOutputFields(OutputFieldsDeclarer declarer) { declarer.declare(new Fields("uuid")); }
```

Σε κάθε spout επομένως θα πρέπει με κάποιο τρόπο να δηλώσω τα πεδία τα οποία θα στείλουμε σε κάποιο η κάποια bolt. Για να μπορέσουμε στο bolt να λάβουμε το tuple το οποίο περιέχει
τα πεδία τα οποία στελνει το spout θα πρέπει να δηλώσει τα πεδία αυτά όπως έγινε παραπάνω και να τα "κατασκευάσει" οταν χρειαστεί για να τα στείλει:
``` //uuid-spout
    @Override
    public void nextTuple() {
        while(true){
            collector.emit(new Values(UUID.randomUUID().toString()));
            Utils.sleep(1000);
        }
    } 
```
Εδώ μέσω του collector στέλνουμε ενα value το οποίο ειναι ενα random uuid. Αυτό ουσιαστικά το έχουμε δηλώσει στον declarer πιο πάνω. Προφανώς το πεδίο "uuid" το οποίο έχουμε 
δηλώσει δεν αποτελεί τπτ άλλο παρα μία ταμπέλα ετσι ώστε να μπορούμε να το πάρουμε στο bolt το οποίο θα συνδεθεί μαζί του. Για παράδειγμα CountBolt 
θα μπορούσε να έχει το εξής:
```
  @Override
    public void execute(Tuple input) {
        String str = input.getValueByField("uuid"); //every function that tuple has returns an object, so a cast is present
        //counters is a hashmap<String,Integer> that keeps the count of every occurence
        if(!counters.containsKey(str)){
            counters.put(str, 1);
        }else {
            Integer c = counters.get(str) +1;
            counters.put(str, c);
        }
    }
```
Επομένως βλέπουμε οτι σε επίπεδο διαχείρησης και παραμετροποίησης του storm, των bolts και των spouts χρειάζονται κάποιες παραμετροποιήσεις οι οποίες καθορίζουν 
και την συμπεριφοραά της τοπολογίας.
Επομένως αν θέλαμε να στείλουμε περισσότερα πεδία προς το bolt θα επρεπε να τα δηλώσουμε, να τους δώσουμε κάποιο label:
```
    public void declareOutputFields(OutputFieldsDeclarer declarer) { declarer.declare(new Fields("uuid", "value", "timestamp")); }
```

Επομένως στο storm θα πρέπει να ορίζουμε κάθε φορά σε κάθε spout/bolt ενα συνολο απο πεδία τα οποία θα θέλουμε να στείλουμε στον επόμενο.
Αρα θα πρέπει να δινουμε την δυνατότητα στον χρήστη να ορίζει αυτά τα πεδία με κάποιο τρόπο ετσι ώστε να τα περνάμε στην συνάρτηση του declarer.

Επιπρόσθετα αν θέλουμε ενα spout/bolt να στέλνει σε πολλούς επόμενους και όχι σε έναν μόνο θα πρέπει να γράψουμε το εξής στην συναρτηση declareOutputFields:
```//uuid-spout
    public void declareOutputFields(OutputFieldsDeclarer declarer) { 
        declarer.declare("stream-1" ,new Fields("uuid", "value", "timestamp")); 
        declarer.declare("stream-2" ,new Fields("uuid", "value", "timestamp")); 
    }
```
Ετσι ο declarer γνωρίζει οτι στέλνει σε δυο διαφορετικά streams και αυτό έχει αντίκτυπο στην τοπολογία. Για παράδειγμα αν θέλαμε το uuid-spout να στέλνει και σε ενα αλλο bolt 
το οποίο περνάει τις τιμές σε μια βάση τοτε θα μπορούσε να γίνει έτσι:
```     
    TopologyBuilder builder = new TopologyBuilder();
    builder.setSpout("uuid-spout", new UUIDSpout());
    builder.setBolt("counter", new CountBolt(), 2).fieldsGrouping("uuid-spout","stream-1", new Fields("uuid"));  // Εδώ δημιουργούμε μια σύνδεση η οποία δέχεται δεδομένα απο το stream-1
    builder.setBolt("db-logger", new MongoDBLogger()).shuffleGrouping("uuid-spout","stream-2"); // Τα ίδια αλλα σύνδεση απο το stream-2
    LocalCluster cluster = new LocalCluster();
    cluster.submitTopology("test",cnf , builder.createTopology());
```
Επομένως αν θελήσουμε να σπάσουμε την ροή σε δύο επόμενες ροές πρέπει κάπως έτσι να δημιουργήσουμε την ροή απο το spout -> στο bolt-1 και απο το spout-> bolt-2
Αρα χρειαζόμαστε ενα stream-id για την κάθε ροή την οποία θα δημιουργήσουμε και ενα σύνολο απο Fields τα οποία θα πρέπει να εκπέμπουμε.
Κάθε bolt/spout το οποίο στέλνει σε παραπάνω απο μια ροή θα πρέπει να υποστηρίζει και να δίνει στον χρηστη την δυνατότητα να παραμετροποιήσει αυτές τις πληροφορίες 
και να προωθεί κατάλληλα στο σωστό bolt τα πεδία.

Γενικά κάθε spout/bolt είναι υπεύθυνο για την υλοποίηση κάποιων πράξεων. Για παράδειγμα ενα spout το οποίο καταναλώνει μηνύματα απο μια ουρά δεδομένων θα πρέπει να 
έχει εναν τρόπο με τον οποίο θα "σπάσει" τα δεδομένα της ροής και θα τα κάνει map σε συγκεκριμένα Objects. Γενικα αν μια ροή περιλαμβάνει ενα μήνυμα της μορφής 
<string>,<number>,<timestamp> τότε θα πρέπει να εφαρμόσουμε κάποιου τύπου pattern matching στα δεδομένα της ροής(πχ σε κάθε ","). Επομένως ενα spout θα πρέπει να παίρνει τέτοιου τύπου
patterns και μετέπειτα να μετατρέπει τα δεδομένα στον σωστό τύπο. Εχει σημασία(?) όταν κάνει emit τα tuples να τα κάνει cast στην σωστή primitive class. 
Θεωρούμε οτι δεν θα υποστηριχθεί complex object μέσα στην ροή διότι τότε θα πρέπει να ορίζουμε και custom serializers.

Επομένως θα πρέπει να υπσοτηριχθεί στον constructor ενός spout ο οποίος δεχεται μηνυματα απο την ουρά ενα stream-id, ενα σύνολο απο fields και οι κλάσεις στις οποίες 
θέλουμε να γίνει το mapping. Το ίδιο πράγμα ωστόσο θα πρέπει να γίνεται σ ενα bolt ανεξαρτήτως αλγορίθμου. Για παράδειγμα ενα threshold bolt το οποίο συγκρίνει τις τιμές 
της ροής με μια τιμή που του έχουμε ορίσει θα πρέπει να ξέρει πόσα και ποιά πεδία θα προωθήσει και που. Στην περίπτωση ενός αλγορίθμου threshold ο οποίος λειτπουργεί 
και σαν φιλτρο υπάρχει ενα σύνολο πράξεων που θα θέλαμε να κάνουμε για αυτά που περνάνε το κατώφλι και ενα σύνολο πράξεων για αυτά που δεν το περνάνε. 
Επομένως λόγω του αλγορίθμου η ροή σίγουρα διχοτομείται με τις τιμές που περνάνε πανω απο το κατώφλι και για τις τιμές που δεν τις περνάνε.
Επομένως πρέπει να ορίσουμε ενα σύνολο πράξεων με stream-id, fields ανάλογα με το τι θέλουμε να κάνουμε.

Γενικά απ ότι φαίνεται ενα σύνολο πράξεων ορίζεται για τα spouts και για τα bolts.
Επομένως δημιουργήθηκε ενα σύνολο κλάσεων τυπου action για να διαχειρίζονται αυτό την 

