package com.Naos.Boxed;

import java.util.ArrayList;
import java.util.List;

public class VolatileAtomicList<E> {
    volatile List<E> Items = new ArrayList<E>();

    public void Clear() {
        GetSetRemove(null,0,4);
    }

    public List<E> Get() {
        return GetSetRemove(null, 0, 0);
    }

    public void Set(int iPlace, E obj) {
        GetSetRemove(obj, iPlace, 1);
    }

    public void Add(E obj) {
        GetSetRemove(obj, 0, 2);
    }

    public void Remove(int iPlace) {
        GetSetRemove(null, iPlace, 3);
    }

    private synchronized List<E> GetSetRemove(E obj, int iPlace, int iGetSetAddRem) {
        //  0:Get 1:Set 2:Add 3:Remove
        if (iGetSetAddRem == 1) {
            Items.set(iPlace, obj);
        } else if (iGetSetAddRem == 2) {
            Items.add(obj);
        } else if (iGetSetAddRem == 3) {
            Items.remove(iPlace);
        } else if (iGetSetAddRem == 4) {
            Items.clear();
        }
        return Items;
    }

    @Override
    public String toString() {
        String sOut = "[";
        for (int i = 0; i < Get().size();i++) {
            sOut += Get().get(i) + ", ";
        }
        sOut = sOut.substring(0,sOut.length() - ((sOut.length() > 1)?3:0)) + "]";
        return sOut;
    }
}
