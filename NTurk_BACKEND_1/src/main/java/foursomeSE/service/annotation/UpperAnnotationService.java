package foursomeSE.service.annotation;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.RAnnotations;

public interface UpperAnnotationService<T extends Annotation> {
//    /**
//     * 需要确认这个用户请求get的这个annotation，的确是他自己的（因为username是token来的，所以不可能是别的人的）
//     * */
//    T getOneBy(long taskId, String username, String imgName);
//
//    /**
//     * 给requester用的
//     * 需要确认这个contract对应的任务是这个requester的（没做）
//     * */
//    T getOneBy(long contractId, String imgName);
//
//    /**
//     * 需要确认imgName是存在的（没做）
//     * 需要确认contract存在（因为在add过程中需要setContractId，所以不存在一定会报错）
//     * 需要确认不存在同样taskId和imageName的contract。 (没做)
//     */
////    void addOneBy(long taskId, String username, T annotation);

    /**
     * getById，这个是为了
     * */
    T getById(long id);

    /**
     * 需要确认这个用户请求update的这个annotation是他自己的（没做）
     * 需要这个任务还没有结束（没做）
     * 只修改concrete annotation类里的具体信息，id什么的不动（没做）
     * */
//    void update(T annotation);


    // 感觉上面那些以前写的方法，现在都想删了。。。

    void saveAnnotations(RAnnotations<T> rAnnotations, String username);
}
