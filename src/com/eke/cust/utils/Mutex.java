package com.eke.cust.utils;

public class Mutex {
	private boolean syncLock;  
    
    ////////////////////////////////////////////////  
    //  Constructor  
    ////////////////////////////////////////////////  
  
    public Mutex()  
    {  
        syncLock = false;  
    }  
      
    ////////////////////////////////////////////////  
    //  lock  
    ////////////////////////////////////////////////  
      
    public synchronized void lock()  
    {  
        while(syncLock == true) {  
            try {  
                wait();  
            }  
            catch (Exception e) { 
            };  
        }  
        syncLock = true;  
    }  
  
    public synchronized void unlock()  
    {  
        syncLock = false;  
        notifyAll();  
    }  
}