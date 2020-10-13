References

## Some tutorials

https://www.tutorialspoint.com/dbms/dbms_transaction.htm

- ACID properties
- Schedule, seriazable schedule
- When multiple transactions are executed, you need to control the concurrency of transactions:
    - Lock based protocol
        - Binary lock or Shared+Exclusives locks. 2PL, Strict 2PL. 
        - Issue : deadlocks. => Deadlock detections
    - Timestamp based protocol
        - Kung-Robinson "Optimistic concurrency control". Read, write, validate. 
        - Timestamp-based concurrency control. Thomas write rule. 
        - Multiversion timestamp concurrency control. 
    - Snapshot isolation (most used)

## Classic refs
Concurrency Control in Distributed Database Systems 
1981
PHILIP A. BERNSTEIN AND NATHAN GOODMAN
https://cs.nyu.edu/courses/fall18/CSCI-GA.3033-002/papers/bernstein-goodman.pdf

The Notions of Consistency and Predicate Locks in a Database System 
1976
K.P. Eswaran, J.N. Gray, R.A. Lorie, and I.L. Traiger IBM Research Laboratory San Jose, California
http://people.eecs.berkeley.edu/~kubitron/courses/cs262a-F14/handouts/papers/p624-eswaran.pdf

## Steal the refs of those articles

Decentralizing MVCC by Leveraging Visibility
2017
Xuan Zhou, Xin Zhou, Zhengtai Yu, Hua Guo, Kian-Lee Tan
https://arxiv.org/abs/1704.01355

Serializable Snapshot Isolation in PostgreSQL
2015
(good references!)
https://drkp.net/papers/ssi-vldb12.pdf

## Algos

On Optimistic Methods for Concurrency Control 
1981
algo
H.T. KUNG and JOHN T. ROBINSON
http://daslab.seas.harvard.edu/reading-group/papers/kung.pdf

Creek: Low-latency, Mixed-Consistency Transactional Replication Scheme
2019
algo
Tadeusz Kobus, Maciej Kokociński, Paweł T. Wojciechowski
https://arxiv.org/abs/1907.00748

## P.S:

The book we have for the course contains a whole chapter for transactions and it's better then the slides of the teacher.


