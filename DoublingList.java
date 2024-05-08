import java.util.AbstractSequentialList;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.lang.Math;
/**
 * An implementation of a list which consists of nodes who contain arrays which
 * double in size each time a new node is added to the list.
 * 
 * @author Jackson Eshbaugh
 * @version 04/09/2024
 */
public class DoublingList<E> extends AbstractSequentialList<E> {

    private Node<E> head = new Node<>(), tail = new Node<>();
    private int size = 0, cap = 0, nodes = 0;

    /**
     * Creates a new, empty {@code DoublingList}.
     */
    public DoublingList() {
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Gets the size of the list.
     * 
     * @return the list's size.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Gets a {@link ListIterator} for this list. Use {@code nextIndex} to control where the iterator starts.
     * 
     * 
     * @throws IndexOutOfBoundsException when {@code nextIndex} is out of the bounds of this list ({@code 0} to {@code size()})
     * @param nextIndex the next index that the cursor will point at (i.e., the first call to {@code next()} will
     * return the element at this location
     * @return a ListIterator with {@code nextIndex = nextIndex}
     */
    @Override
    public ListIterator<E> listIterator(int nextIndex) {
        
        if(nextIndex >= size() || nextIndex < 0) throw new IndexOutOfBoundsException
            ("The nextIndex " + nextIndex + " is out of bounds for this list of size " + size());
        
        return new DoublingListIterator(this, nextIndex);
    }

    /**
     * Gets a {@link Iterator} for this list
     * 
     * @return an Iterator for this list.
     */
    @Override
    public Iterator<E> iterator() {
        return (Iterator<E>) new DoublingListIterator(this);
    }
    
    /**
     * Gets a {@link ListIterator} for this list, starting at the beginning of the list.
     * 
     * @throws IndexOutOfBoundsException when the list is at {@code size = 0}
     * @return a ListIterator with {@code nextIndex = 0}
     */
    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    /**
     * Adds the given element {@code element} to the end of the list.
     * 
     * @param element the element to add to the list
     * @return {@code true} if the operation was successful
     */
    @Override
    public boolean add(E element) {
        add(size, element);
        return true;
    }

    /**
     * Adds the given element {@code element} to the list at index {@code index}.
     * 
     * @param index the index to add {@code element} at
     * @param element the element to add to the list at {@code index}
     */
    @Override
    public void add(int index, E element) {

        if(element == null) throw new NullPointerException("Null elements are not permitted.");

        if(size < cap) {
            // CASE 1
            // At least one of the nodes has available slots.
            // cap must be > 0

            if(index <= size - 1) {
                NodeInfo info = find(index);

                if(!info.node.isFull()) {
                    info.node.addShift(info.offset);
                    info.node.values[info.offset] = element;
                    info.node.setSize(info.node.size + 1);
                    size++;
                    return;
                }

                // info.node is full.
                // Check if a predecessor has space

                Node<E> curr = info.node;

                while(curr != head && curr.isFull()) curr = curr.prev;

                if(curr != head) {

                    // Found a predecessor with space.
                    leftShift(index - 1);

                    info.node.values[info.offset - 1] = element;
                    info.node.setSize(info.node.size + 1);
                    size++;
                    return;
                }

                // Must be a successor node with space
                rightShift(index);
                info.node.values[info.offset] = element;
                info.node.setSize(info.node.size + 1);
                size++;
                return;
            }

            // index == size

            Node<E> curr = tail;
            while(curr.size == 0) curr = curr.prev;

            if(!curr.isFull()) {
                curr.values[curr.size] = element;
                curr.size++;
                size++;
                return;
            }

            // curr is full
            // Check if a predecessor of curr has space

            Node<E> curr2 = curr;
            while(curr2.isFull() && curr2 != head) curr2 = curr2.prev;

            if(curr2 != head) {
                // A predecessor has space
                leftShift(index - 1);

                curr.values[curr.size] = element;
                curr.size++;
                size++;
                return;
            }

            // Else, an empty successor must exist.
            curr = curr.next;
            curr.values[curr.size] = element;
            curr.size++;
            size++;
            return;
        }

        // CASE 2: size == cap
        // All nodes in the list are full

        Node<E> newNode = new Node((int) Math.pow(2, nodes));
        nodes++;
        cap = (int) Math.pow(2, nodes) - 1;

        newNode.next = tail;
        tail.prev.next = newNode;
        newNode.prev = tail.prev;
        tail.prev = newNode;

        if(index == size) {
            newNode.values[0] = element;
            size++;
            newNode.size++;
            return;
        }

        // else, index < size

        NodeInfo info = find(index);

        rightShift(index);

        info.node.values[info.offset] = element;
        info.node.setSize(info.node.size + 1);
        size++;
        return;
    }

    /**
     * Removes and returns the element at {@code index} from the list.
     * 
     * @param index the index where the removal should occur
     * @return the removed element
     */
    @Override
    public E remove(int index) {

        // Find and save the element at index
        NodeInfo info = find(index);
        E temp = info.node.values[info.offset];

        info.node.values[info.offset] = null;
        info.node.setSize(info.node.size - 1);
        size--;

        if(size() == 0) {
            // List is empty, replace it with a (new) empty list
            head.next = tail;
            tail.prev = head;
            cap = 0;
            nodes = 0;
            return temp;
        }

        info.node.removeShift(info.offset);

        if(size() <= Math.pow(2, nodes - 2) - 1) {

            // List has too much empty space spread across nodes, so compact this list.

            // Save the old list
            Node<E> curr = head.next, working = new Node(1);

            // Setup like a new list
            head.next = tail;
            tail.prev = head;
            size = 0;
            nodes = 0;
            cap = 0;

            // Loop through the old list and add each element to the new list.

            while(curr != tail) {
                for(int i = 0; i < curr.size; ++i) {
                    this.add(curr.values[i]);
                }
                curr = curr.next;
            }

            // Add the extra empty node on the end
            Node<E> newNode = new Node((int) Math.pow(2, nodes));
            nodes++;
            cap = (int) Math.pow(2, nodes) - 1;

            newNode.next = tail;
            tail.prev.next = newNode;
            newNode.prev = tail.prev;
            tail.prev = newNode;

        }

        return temp;
    }

    /**
     * Shift each element (up to and including the element at {@code index}) to the left.
     * Leaves a final duplicate of the element at {@code index} which can be ignored for these use cases (adding
     * to the list)
     * 
     * @param index the logical index where the shift should occur.
     */
    private void leftShift(int index) {

        NodeInfo finish = find(index);

        // assume there is a node before finish.node that has an empty space.
        // then, shift each element with a logical index <= that of startPoint to the left by one.

        // First, identify the rightmost node that is before this node that has space.

        Node<E> rightmostNode = finish.node.prev;

        while(rightmostNode.prev != head && rightmostNode.isFull()) rightmostNode = rightmostNode.prev;

        // Then, identify the index of the first null slot in this node.

        int firstNullOffset = rightmostNode.size;

        // Finally, find the next logical index in the list and move this element to the empty slot,
        // doing this until we reach finish.

        NodeInfo placeholder1 = new NodeInfo(rightmostNode, firstNullOffset);
        NodeInfo placeholder2 = new NodeInfo(rightmostNode.next, 0);

        // this node will grow by a size of 1, so premptively increase the size.
        placeholder1.node.setSize(placeholder1.node.size + 1);

        // We know all slots in all nodes up to and including finish are filled, so we can start here and
        // increment up to finish.

        while(!placeholder2.equals(finish)) {

            placeholder1.node.values[placeholder1.offset] = placeholder2.node.values[placeholder2.offset];
            placeholder1 = placeholder2;

            // Check if we're at the end of the node in placeholder2
            if(placeholder2.node.values.length - 1 == placeholder2.offset) {
                placeholder2 = new NodeInfo(placeholder2.node.next, 0);
            } else {
                placeholder2 = new NodeInfo(placeholder2.node, placeholder2.offset + 1);
            }
        }

        // Shift the final element
        placeholder1.node.values[placeholder1.offset] = placeholder2.node.values[placeholder2.offset];
        // make space in size for the next element. 
        placeholder2.node.setSize(placeholder2.node.size - 1);
    }

    /**
     * Shift each element (starting with the element at {@code index}) to the right.
     * Leaves a final duplicate of the element at {@code index} which can be ignored for these use cases (adding
     * to the list)
     * 
     * @param index the logical index where the shift should occur
     */
    private void rightShift(int index) {

        NodeInfo finish = find(index);
        // assume there is a node after finish.node that has an empty space.
        // then, shift each element with a logical index >= that of startPoint to the right by one.

        // First, identify the leftmost node that is after this node that has space.

        Node<E> leftmostNode = finish.node;

        while(leftmostNode.isFull()) leftmostNode = leftmostNode.next;

        // Then, identify the index of the first null slot in this node.

        int firstNullOffset = leftmostNode.size;

        // Finally, find the previous logical index in the list and move this element to the empty slot,
        // doing this until we reach finish

        NodeInfo placeholder2;

        NodeInfo placeholder1 = new NodeInfo(leftmostNode, firstNullOffset);
        if(firstNullOffset == 0)
            placeholder2 = new NodeInfo(leftmostNode.prev, leftmostNode.prev.size - 1);
        else
            placeholder2 = new NodeInfo(leftmostNode, firstNullOffset - 1);

        // Premtively increase placeholder1's node's size
        placeholder1.node.setSize(placeholder1.node.size + 1);
            
        // We know all slots in all nodes up to and including finish are filled, so we can start here and
        // move backward through the list to finish.

        while(!placeholder2.equals(finish)) {

            placeholder1.node.values[placeholder1.offset] = placeholder2.node.values[placeholder2.offset];
            placeholder1 = placeholder2;

            // Check if we're at the front of the node in placeholder2
            if(placeholder2.offset == 0) {
                placeholder2 = new NodeInfo(placeholder2.node.prev, placeholder2.node.prev.size - 1);
            } else {
                placeholder2 = new NodeInfo(placeholder2.node, placeholder2.offset - 1);
            }
        }

        // Shift the final element
        placeholder1.node.values[placeholder1.offset] = placeholder2.node.values[placeholder2.offset];
        placeholder2.node.setSize(placeholder2.node.size - 1);

    }

    /**
     * Fetches the {@link DoublingList#NodeInfo} object (node and offset) of a
     * given {@code logicalIndex}.
     * 
     * @param logicalIndex the logicalIndex to locate
     * @return the {@code NodeInfo} that corresponds to the given {@code logicalIndex} 
     */
    private NodeInfo find(int logicalIndex) {

        // Edge case handling
        if(logicalIndex < 0 || logicalIndex >= size) throw new IndexOutOfBoundsException("Index " + logicalIndex + " out of bounds for cap " + cap + ".");

        Node<E> curr = head.next;
        int cumulativeSize = 0;

        while (curr != tail) {
            if(cumulativeSize + curr.size > logicalIndex) {
                // The logicalIndex is in this node.
                return new NodeInfo(curr, logicalIndex - cumulativeSize);
            }

            cumulativeSize += curr.size;
            curr = curr.next;
        }

        throw new NoSuchElementException("Logical index " + logicalIndex + " not found in the list. This should not happen.");
    }

    /**
     * Returns each list as a String representation, using "—" to identify empty ({@code null}) array spaces.
     * 
     * @return the String representation of the list
     */
    public String toStringInternal() {

        if(size() == 0) return "[]";

        StringBuilder builder = new StringBuilder("[");

        Node<E> curr = head;
        while(curr.next != tail) {
            curr = curr.next;
            builder.append("(");

            for(E e : curr.values) {
                if(e == null) builder.append("—, ");
                else builder.append(e.toString() + ", ");
            }

            String str = builder.toString();
            builder = new StringBuilder(str.substring(0, str.length() - 2));

            builder.append("), ");
        }

        String str = builder.toString();
        builder = new StringBuilder(str.substring(0, str.length() - 2));
        builder.append("]");

        return builder.toString(); 
    }

    /**
     * Returns each list as a String representation, using "—" to identify empty ({@code null}) array spaces and "|" to show
     * the iterator's pointer location.
     * 
     * @param iter the {@code ListIterator}
     * @return the String representation of the list
     */
    public String toStringInternal(ListIterator iter) {
        if(size() == 0) return "[]";

        int logicalIndex = 0;
        StringBuilder builder = new StringBuilder("[");

        Node<E> curr = head;
        while(curr.next != tail) {
            curr = curr.next;
            builder.append("(");

            for(E e : curr.values) {
                if(e == null) builder.append("—, ");
                else {
                    if(iter.nextIndex() == logicalIndex)
                        builder.append("| ");
                    builder.append(e.toString() + ", ");
                    ++logicalIndex;
                }
            }

            String str = builder.toString();
            builder = new StringBuilder(str.substring(0, str.length() - 2));

            builder.append("), ");
        }

        String str = builder.toString();
        builder = new StringBuilder(str.substring(0, str.length() - 2));
        if(iter.nextIndex() >= size()) builder.append("|");
        builder.append("]");

        return builder.toString();
    }
    
    /**
     * Compacts the list down to save memory. To be used by {@link #remove(int)}.
     */
    private void compact() {
        // List has too much empty space spread across nodes, so compact this list.

        // Save the old list
        Node<E> curr = head.next, working = new Node(1);

        // Setup like a new list
        head.next = tail;
        tail.prev = head;
        size = 0;
        nodes = 0;
        cap = 0;

        // Loop through the old list and add each element to the new list.

        while(curr != tail) {
            for(int i = 0; i < curr.size; ++i) {
                this.add(curr.values[i]);
            }
            curr = curr.next;
        }

        // Add the extra empty node on the end
        Node<E> newNode = new Node((int) Math.pow(2, nodes));
        nodes++;
        cap = (int) Math.pow(2, nodes) - 1;

        newNode.next = tail;
        tail.prev.next = newNode;
        newNode.prev = tail.prev;
        tail.prev = newNode;
    }

    /**
     * An object representing a single array ("node") in the larger {@link DoublingList}.
     * 
     * @author Jackson Eshbaugh
     * @version 04/03/2024
     */
    private class Node<E> {
        E[] values;
        Node<E> next, prev;
        int size = 0;

        /**
         * Creates a node with a values array of length 0.
         * For use in creating the head and tail node.
         */
        Node() {
            values = (E[]) new Object[0];
        }

        /**
         * Creates a node with a values array of length {@code capacity}.
         * 
         * @param capacity the size that this node's array should be initialized to
         */
        Node(int capacity) {
            values = (E[]) new Object[capacity];
        }
        
        /**
         * Sets the size of the node.
         * 
         * @throws IndexOutOfBoundsException when the {@code size >} the length of the values array
         * @param size the size to set the node to.
         */
        void setSize(int size) {
            if(size > values.length) throw new IndexOutOfBoundsException("size > length of values");
            this.size = size;
        }

        /**
         * Checks if the node's values array is full.
         * 
         * @return {@code true} if the node is full, {@code false} otherwise
         */
        boolean isFull() {
            return size == values.length;
        }

        /**
         * For use in {@link DoublingList#add(int, E)}.
         * Shifts each value in the array starting at index {@code offset} up by one.
         * 
         * @throws IllegalStateException if the {@code values} array is full when invoked
         * @param offset the offset (index of the {@code values} array) to finish this shift at
         */
        void addShift(int offset) {

            if(isFull()) throw new IllegalStateException("The array is full.");

            for(int i = this.size - 1; i >= offset; --i) {
                values[i + 1] = values[i]; 
            }
        }

        /**
         * For use in {@link DoublingList#remove(int)}.
         * Shifts each value in the array down to and including index {@code offset} down by one.
         * Sets the resulting empty slot in the array to {@code null}
         * 
         * @param offset the offset (index of the {@code values} array) to start this shift at
         */
        void removeShift(int offset) {

            for(int i = offset + 1; i < this.size + 1; ++i) {
                values[i - 1] = values[i]; 
            }

            values[this.size] = null;
        }

    }

    /**
     * A list iterator for the {@link DoublingList} object.
     * 
     * @author Jackson Eshbaugh
     * @version 04/09/2024
     */
    private class DoublingListIterator implements ListIterator<E> {

        private int nextIndex = 0; // Cursor
        private boolean nextLastCalled = false, previousLastCalled = false;
        private boolean removeCalled = false, addCalled = false;
        private DoublingList list;

        
        /**
         * Creates a new DoublingListIterator that starts at
         * {@code nextIndex = 0}.
         * 
         * @param list the list that this iterator iterates over
         */
        public DoublingListIterator(DoublingList list) {
            this.list = list;
        }
        
        /**
         * Creates a new DoublingListIterator starting with {@code nextIndex}.
         * 
         * @param list the list that this iterator iterates over
         * @param nextIndex the next index in the list (i.e. the index where you'd like to start + 1)
         */
        public DoublingListIterator(DoublingList list, int nextIndex) {
            this.nextIndex = nextIndex;
            this.list = list;
        }
        
        @Override
        public boolean hasNext() {
            return nextIndex < size();
        }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException("No next element exists.");
            
            NodeInfo info = list.find(nextIndex);
            
            nextIndex++;
            previousLastCalled = false;
            nextLastCalled = true;
            removeCalled = false;
            addCalled = false;
            return info.node.values[info.offset];
        }

        @Override
        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        @Override
        public E previous() {
            if (!hasPrevious()) throw new NoSuchElementException("No previous element exists.");

            nextIndex--;
            NodeInfo info = list.find(nextIndex);
            previousLastCalled = true;
            nextLastCalled = false;
            removeCalled = false;
            addCalled = false;
            return info.node.values[info.offset];
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex - 1;
        }

        @Override
        public void remove() {

            if(!nextLastCalled && !previousLastCalled)
                throw new IllegalStateException("You must call next() or previous() before invoking this method.");
                
            if(addCalled || removeCalled)
                throw new IllegalStateException("You can't call add(E) or remove() before invoking this method without calling next() or previous().");

            int index = nextLastCalled ? nextIndex - 1 : nextIndex;
            list.remove(index);
            removeCalled = true;

            if(nextLastCalled) nextIndex--;
        }

        @Override
        public void set(E element) {
            if(!nextLastCalled && !previousLastCalled) throw new IllegalStateException("You must call next() or previous() before invoking this method.");
            if(addCalled || removeCalled) throw new IllegalStateException("You can't call add(E) or remove() before invoking this method without calling next() or previous().");

            if(nextLastCalled) {
                // replace the previous value with element.
                NodeInfo info = list.find(nextIndex - 1);
                info.node.values[info.offset] = element;
                return;
            }

            // replace the next value with element.
            NodeInfo info = list.find(nextIndex);
            info.node.values[info.offset] = element;
        }

        @Override
        public void add(E element) {
            list.add(nextIndex, element);
            nextIndex++;

            addCalled = true;
        }
    }

    /**
     * Holds basic info pointing to a specific element in a node, including the {@code node} itself and
     * the {@code offset} (or the array index) of the element.
     * 
     * @author Jackson Eshbaugh
     * @version 04/03/2024
     */
    private class NodeInfo {

        public Node<E> node;
        public int offset;

        /**
         * Creates a new {@code NodeInfo} object, with {@code node = node} and {@code offset = offset}. 
         * 
         * @param node the {@link DoublingList#Node} that the element appears in
         * @param offset the offset of the element in the node (i.e., the index in the {@code values}
         * array in the {@code node}.)
         */
        public NodeInfo(Node<E> node, int offset) {
            this.node = node;
            this.offset = offset;
        }

        @Override
        public boolean equals(Object o) {

            if(o == null) return false;

            if(!(o instanceof DoublingList.NodeInfo)) return false;

            NodeInfo info = (NodeInfo) o;

            return (info.node == this.node) && (info.offset == this.offset);

        }

    }

}
