package test;

import com.pinyougou.common.IdWorker;

public class Test {
    public static void main(String[] args) {
        IdWorker idWorker = new IdWorker();
        long l = idWorker.nextId();
        System.out.println(l);
    }
}
