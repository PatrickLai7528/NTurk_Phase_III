//package foursomeSE.service.common;
//
//import foursomeSE.util.FileUtil;
//import foursomeSE.util.TRef;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Consumer;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//public abstract class CommonCongruentServiceImpl<T> extends CommonServiceImpl<T, T> implements CommonCongruentService<T> {
//    @Override
//    protected T sToD(T s) {
//        return s;
//    }
//
//    @Override
//    protected T dToS(T d) {
//        return d;
//    }
//
//    //    protected List<T> records;
////
////    public CommonCongruentServiceImpl() {
////        this.records = readAll();
////    }
////
////    @Override
////    public T getById(long id) {
////        return getOneBy(t -> getIdFunction().apply(t) == id);
////    }
////
////    @Override
////    public T getOneBy(Predicate<T> p) {
////        TRef<T> result = new TRef<>();
////        records.stream()
////                .filter(p)
////                .findAny()
////                .ifPresent(t -> result.t = t);
////        return result.t;
////    }
////
////    @Override
////    public List<T> getLotBy(Predicate<T> p) {
////        return records.stream()
////                .filter(p)
////                .collect(Collectors.toCollection(ArrayList::new));
////    }
////
////    @Override
////    public void add(T record) {
////        doBeforeAdding(record);
////
////        long[] newId = {0};
////        records.stream()
////                .mapToLong(t -> getIdFunction().apply(t))
////                .max()
////                .ifPresent(m -> newId[0] = m + 1);
////        setIdFunction(newId[0]).accept(record);
////
////        records.add(record);
////        saveAll();
////    }
////
////    @Override
////    public void update(T record) {
////        doBeforeUpdating(record);
////
////        T oldRecord = getById(getIdFunction().apply(record));
////        if (oldRecord != null) {
////            records.remove(oldRecord);
////            records.add(record);
////            saveAll();
////        }
////    }
////
////    @Override
////    public void delete(long id) {
////        T oldRecord = getById(id);
////        records.remove(oldRecord);
////        saveAll();
////    }
////
////    /**
////     * File io
////     */
////    private List<T> readAll() {
////        return FileUtil.readAll(getTArrayType(), getTableName());
////    }
////
////    private void saveAll() {
////        FileUtil.saveAll(records, getTableName());
////    }
////
////    /**
////     * to be overridden
////     */
////    protected void doBeforeAdding(T record) {
////    }
////
////    protected void doBeforeUpdating(T record) {
////    }
////
////
////    /**
////     * to be implemented
////     */
////
////    protected abstract String getTableName();
////
////    protected abstract Class<T[]> getTArrayType();
////
////    protected abstract Function<T, Long> getIdFunction();
////
////    protected abstract Consumer<T> setIdFunction(long newId);
//}
