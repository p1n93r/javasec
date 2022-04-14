//package com.pinger.javasec.jep290.filter;
//
//import com.pinger.javasec.jep290.pojo.User;
//import sun.misc.ObjectInputFilter;
//
///**
// * @author : p1n93r
// * @date : 2021/9/23 15:08
// * 自定义一个反序列化过滤器
// */
//public class CustomObjectInputFilter implements ObjectInputFilter {
//    private static final Class<?> CLAZZ = User.class;
//    /**
//     * 限制反序列化类数组的数组大小
//     */
//    private static final long ARRAY_LENGTH = -1L;
//
//    /**
//     * 限制反序列化时，引用的数量
//     */
//    private static final long TOTAL_OBJECT_REFS = 1L;
//
//    /**
//     * 限制反序列化的深度
//     */
//    private static final long DEPTH = 1l;
//
//    /**
//     * 限制反序列化流的大小
//     */
//    private static final long STREAM_BYTES = 95L;
//
//    @Override
//    public Status checkInput(FilterInfo filterInfo) {
//        if (filterInfo.arrayLength() != ARRAY_LENGTH
//            || filterInfo.references() != TOTAL_OBJECT_REFS
//            || filterInfo.depth() != DEPTH
//            || filterInfo.streamBytes() != STREAM_BYTES
//        ) {
//            return Status.REJECTED;
//        }
//
//        if (filterInfo.serialClass() == null) {
//            return Status.UNDECIDED;
//        }
//
//        if (filterInfo.serialClass() != null && filterInfo.serialClass() == CLAZZ) {
//            return Status.ALLOWED;
//        }
//        return Status.REJECTED;
//    }
//}
