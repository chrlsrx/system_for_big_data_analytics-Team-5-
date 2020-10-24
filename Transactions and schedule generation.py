# We assume that we have a list of lists of transactions
# Each transaction is a list of reads and writes with the index of the transaction
# In the real implementaion, a transaction will be a class returning a list of operations, but the concept is the same

def generate_transaction(k):
    Transaction = [f'{k}Read12',f'{k}Write(12,x)']
    return Transaction

# A schedule is first defined by the number of the transactions it represents
N = 5
schedule = []

# The complexity of the schedule involves the maximum number of active transactions
# We want to simulate the effect of having multiple agents accessing the database at the same time
# We will define a function that generates schedules with that in mind
max_active_transactions = 3

schedule = []
active_transactions =[]
progress_pointers = []
generated_transactions = 0
cond = True
from random import randint
while generated_transactions<N:
    if len(active_transactions) < max_active_transactions:
        generated_transactions += 1
        active_transactions.append(generate_transaction(generated_transactions))
        progress_pointers.append(0)
    k = randint(0, len(active_transactions)-1)
    schedule.append(active_transactions[k][progress_pointers[k]])
    progress_pointers[k] += 1
    if progress_pointers[k] == len(active_transactions[k]):
        progress_pointers.pop(k)
        active_transactions.pop(k)

while len(progress_pointers)>0:
    k = randint(0, len(active_transactions)-1)
    schedule.append(active_transactions[k][progress_pointers[k]])
    progress_pointers[k] += 1
    if progress_pointers[k] == len(active_transactions[k]):
        progress_pointers.pop(k)
        active_transactions.pop(k)

print(schedule)

# This is a basic schedule generator, depending on the structure of the transaction object, we can make more sophisticated versions
# Paths for improvements:
# Having an object stress indicator for transactions: if there are reads and writes of the same few objects, it makes the schedule more complicated
# Having many types of transactions and choosing each time what type to operate

