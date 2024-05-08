Problems:

1. My insertion was off by one (my logical indicies were behaving as though they were 1-based). I found that
my find() method used a cumulativeSize starting at -1, which accounted for the issue. Now, insertion is ordered
correctly.

2. Inserting at 0 threw an ArrayIndexOutOfBoundsException, because it attempted a left shift instead of a
right shift. This all happened because I iterated forward through the nodes, then found one with room and thought
it was a previous node. Also, rightShift() tried to find the leftmost successor node to the current one while
iterating backwards (insert facepalm here).

3. When shifting for remove, I added one to the offset in two different places, which caused my shift algorithm
to not shift as expected. Since the algorithm is only meant for use with remove(), I simply kept the add one logic
in the method, so I wouldn't confuse myself later.

4. My leftShift() algorithm was called incorrectly, and with some much appreciated help from a great professor,
we narrowed the issue down to an oversight: I was calling leftShift(index) and not leftShift(index - 1).

5. Similarly, my rightShift() was messed up, this time I started at the beginning of the previous node, though
I was actually iterating backwards. Starting at the end of the node (node.size - 1) fixed that right up.

6. The logic for updating the size of the nodes in the leftShift() method was messed up. It turns out that I needed to
increment the size of the node with empty space (where I'm shifting to) and decrement the size of the node where the
shift finishes.

7. The add(index, E) function wasn't working, but it came down to a small mistake in the size updating for my
rightShift() method—I needed to decrement the size of the node where the new element will be added (and not the
node before it). Confused my two pointers!

---

Experience:

I really honed in my troubleshooting skills. It took many hours to fully debug this project and I really learned
the importance of whiteboarding tests or working things out on paper. It was a practical example of the design
side of CS, something I've always integrated into my coding process. Part of what I'm learning in this course is
how to decouple these processes and I'm so glad—I want to be able think computer science, not just be able to
do it.