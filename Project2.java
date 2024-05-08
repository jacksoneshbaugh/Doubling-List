import java.util.ListIterator;

/**
 * A driver for the {@link DoublingList} class, testing its functionality.
 * 
 * @author Jackson Eshbaugh
 * @version 04/14/2024
 */
public class Project2 {
    
    /**
     * Driver method for {@link DoublingList}.
     * 
     * @param args not used
     */
    public static void main(String[] args) {
        
        DoublingList<String> list = new DoublingList<String>();
        
        System.out.println(list.toStringInternal());
        
        list.add("A");
        list.add("B");
        list.add("C");
        
        System.out.println(list.toStringInternal());
        
        list.add(0, "X");
        System.out.println(list.toStringInternal());
        list.add(2, "Y");
        System.out.println(list.toStringInternal());
        list.add(1, "Z");
        System.out.println(list.toStringInternal());
        
        System.out.println(list.remove(2));
        System.out.println(list.toStringInternal());
        System.out.println(list.remove(1));
        System.out.println(list.toStringInternal());
        
        // Iterating
        
        ListIterator<String> iter = list.listIterator();
        
        while(iter.hasNext()) {
            System.out.println(list.toStringInternal(iter));
            System.out.println(iter.next());
        }
        
        while(iter.hasPrevious()) {
            System.out.println(iter.previous());
            System.out.println(list.toStringInternal(iter));
        }
        
        iter.next();
        iter.add("G");
        System.out.println(list.toStringInternal(iter));
        
        iter.previous();
        iter.previous();
        iter.remove();
        System.out.println(list.toStringInternal(iter));

    }
    
}