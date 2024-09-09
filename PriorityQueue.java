/*  Student information for assignment:
 *
 *  On our honor, Mahir Kaya and Ayman Mahfuz, this programming assignment is our own work
 *  and we have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1 (Student whose Canvas account is being used)
 *  UTEID: mk45397
 *  email address: mahirgs2005@gmail.com
 *  Grader name: Namish
 *
 *  Student 2
 *  UTEID: aam7544
 *  email address: aymanmahfuz27@utexas.edu
 *
 */

//CHANGE THIS FOR STYLE
import java.util.LinkedList;
import java.util.Iterator;


public class PriorityQueue<E extends Comparable<E>> {
    LinkedList<E> con;

    /**
     * Construct the internal storage container
     */
    public PriorityQueue() {
        con = new LinkedList<>();
    }


    /**
     * Enqueue the element in a priority based way
     * @param other, the element to be enqueued
     */
    public void enqueue(E other) {
        int index = 0;
        Iterator<E> it = con.iterator();
        boolean flag = true;
        //Adds the new tree to the queue  in a priority way
        while(it.hasNext() && flag) {
            E current = it.next();
            if (current.compareTo(other) <= 0 ) {
                 index++;
            } else {
                flag = false;
            }
        }
        con.add(index, other);
    }


    /**
     * Remove and return the first element in the list
     */
    public E dequeue() {
        return con.remove(0);
    }

    /**
     * @return the size of the queue
     */
    public int size() {
        return con.size();
    }


}

