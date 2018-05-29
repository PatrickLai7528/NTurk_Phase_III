//package foursomeSE.service.common;
//
//import foursomeSE.error.MyObjectNotFoundException;
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
//public abstract class CommonServiceImpl<D, S> implements CommonService<D, S> {
//    protected List<S> records;
//
//    public CommonServiceImpl() {
////        this.records = readAll();
//    }
//
//    // 所有的单个get，和update，和delete都可能会throw MyObjectNotFoundException
//    @Override
//    public D getById(long id) {
////        System.out.println("CommonServiceImpl, getById: id:" + id);
//        return sToD(getSById(id));
//    }
//
//    @Override
//    public D getOneBy(Predicate<S> p) {
//        return sToD(getSOneBy(p));
//    }
//
//    @Override
//    public List<D> getLotBy(Predicate<S> p) {
//        readAll();
//
//        return records.stream()
//                .filter(p)
//                .map(this::sToD)
//                .collect(Collectors.toCollection(ArrayList::new));
//    }
//
//    @Override
//    public void add(D record) {
//        readAll();
//        doBeforeAdding(record);
//
//        long[] newId = {0};
//        records.stream()
//                .mapToLong(t -> getIdFunction().apply(t))
//                .max()
//                .ifPresent(m -> newId[0] = m + 1);
//        setIdFunction(newId[0]).accept(record);
//
//        records.add(dToS(record));
//        saveAll();
//    }
//
//    @Override
//    public void update(D record) {
//        readAll();
//        doBeforeUpdating(record);
//
//        S oldRecord = getSById(getIdFunction().apply(dToS(record)));
//        records.remove(oldRecord);
//        records.add(dToS(record));
//        saveAll();
//    }
//
//    @Override
//    public void delete(long id) {
//        readAll();
//
//        S oldRecord = getSById(id);
//        records.remove(oldRecord);
//        saveAll();
//    }
//
//    /**
//     * protected
//     */
//    protected <Y, X> List<Y> mapList(List<X> xs, Function<X, Y> f) {
//        return xs.stream().map(f).collect(Collectors.toCollection(ArrayList::new));
//    }
//
//    protected S getSById(long id) {
//        return getSOneBy(t -> getIdFunction().apply(t) == id);
//    }
//
//    protected S getSOneBy(Predicate<S> p) {
//        readAll();
////        System.out.println("CommenServiceImpl, getSOneBy: p: " + p);
////        System.out.println("CommenServiceImpl, getSOneBy: list: " + records);
//
//        TRef<S> result = new TRef<>();
//        records.stream()
//                .filter(p)
//                .findAny()
//                .ifPresent(t -> result.t = t);
//
//        if (result.t == null) {
//            throw new MyObjectNotFoundException("this is legacy code of iteration 2. How come that you come here?");
//        }
//        return result.t;
//    }
//
//    protected List<S> getSLotBy(Predicate<S> p) {
//        readAll();
//
//        return records.stream()
//                .filter(p)
//                .collect(Collectors.toCollection(ArrayList::new));
//    }
//
//    /**
//     * private
//     */
//
//    private void readAll() {
//        records = FileUtil.readAll(getTArrayType(), getTableName());
//    }
//
//    private void saveAll() {
//        FileUtil.saveAll(records, getTableName());
//    }
//
//    /**
//     * to be overridden
//     */
//    protected void doBeforeAdding(D record) {
//    }
//
//    protected void doBeforeUpdating(D record) {
//    }
//
//
//    /**
//     * to be implemented
//     */
//
//    protected abstract D sToD(S s);
//
//    protected abstract S dToS(D d);
//
//    protected abstract String getTableName();
//
//    protected abstract Class<S[]> getTArrayType();
//
//    protected abstract Function<S, Long> getIdFunction();
//
//    protected abstract Consumer<D> setIdFunction(long newId);
//}
