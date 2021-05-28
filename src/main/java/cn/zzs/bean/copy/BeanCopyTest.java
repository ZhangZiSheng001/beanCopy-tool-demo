package cn.zzs.bean.copy;



import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import cn.zzs.bean.copy.other.User;
import cn.zzs.bean.copy.other.UserService;
import cn.zzs.bean.copy.other.UserVO;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import net.sf.cglib.beans.BeanCopier;

/**
 * 测试不同 bean copy 工具
 * @author zzs
 * @date 2020年11月9日 下午3:46:58
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BeanCopyTest {
    /**
     * 被拷贝的源对象
     */
    @State(Scope.Benchmark)
    public static class CommonState {
        User user;
        
        @Setup(Level.Trial)
        public void prepare() {
            user = new UserService().get();
        }
    }
    
    /**
     * 测试手动getter/setter拷贝对象属性
     * @author zzs
     * @date 2020年12月8日 下午3:01:51
     * @param commonState
     * @return UserVO
     * @throws Exception 
     */
    @Benchmark
    public UserVO testDeadCode(CommonState commonState) throws Exception {
        UserVO userVO = new UserVO();
        User user = commonState.user;
        userVO.setAccount(user.getAccount());
        userVO.setAddress(user.getAddress());
        userVO.setAge(user.getAge());
        userVO.setBirthday(user.getBirthday());
        userVO.setDepartment(user.getDepartment());
        userVO.setDiploma(user.getDiploma());
        userVO.setEmail(user.getEmail());
        userVO.setFax(user.getFax());
        userVO.setId(user.getId());
        userVO.setIdCard(user.getIdCard());
        userVO.setInnerTel(user.getInnerTel());
        userVO.setJob(user.getJob());
        userVO.setJoinDate(user.getJoinDate());
        userVO.setLeaved(user.getLeaved());
        userVO.setLoginDate(user.getLoginDate());
        userVO.setMaritalStatus(user.getMaritalStatus());
        userVO.setMobile(user.getMobile());
        userVO.setName(user.getName());
        userVO.setNo(user.getNo());
        userVO.setOuterTel(user.getOuterTel());
        userVO.setPassword(user.getPassword());
        userVO.setPermanentAddress(user.getPermanentAddress());
        userVO.setPicture(user.getPicture());
        userVO.setPinyin(user.getPinyin());
        userVO.setPosition(user.getPosition());
        userVO.setQq(user.getQq());
        userVO.setSex(user.getSex());
        userVO.setStatus(user.getStatus());
        userVO.setType(user.getType());
        userVO.setWeixin(user.getWeixin());
        userVO.setWeixinQrcode(user.getWeixinQrcode());
        userVO.setField00(user.getField00());
        userVO.setField01(user.getField01());
        userVO.setField02(user.getField02());
        userVO.setField03(user.getField03());
        userVO.setField04(user.getField04());
        userVO.setField05(user.getField05());
        userVO.setField06(user.getField06());
        userVO.setField07(user.getField07());
        userVO.setField08(user.getField08());
        userVO.setField09(user.getField09());
        userVO.setField10(user.getField10());
        userVO.setField11(user.getField11());
        userVO.setField12(user.getField12());
        userVO.setField13(user.getField13());
        userVO.setField14(user.getField14());
        userVO.setField15(user.getField15());
        userVO.setField16(user.getField16());
        userVO.setField17(user.getField17());
        userVO.setField18(user.getField18());
        userVO.setField19(user.getField19());
        userVO.setField20(user.getField20());
        userVO.setField21(user.getField21());
        userVO.setField22(user.getField22());
        userVO.setField23(user.getField23());
        userVO.setField24(user.getField24());
        userVO.setField25(user.getField25());
        userVO.setField26(user.getField26());
        userVO.setField27(user.getField27());
        userVO.setField28(user.getField28());
        userVO.setField29(user.getField29());
        userVO.setField30(user.getField30());
        userVO.setField31(user.getField31());
        userVO.setField32(user.getField32());
        userVO.setField33(user.getField33());
        userVO.setField34(user.getField34());
        userVO.setField35(user.getField35());
        userVO.setField36(user.getField36());
        userVO.setField37(user.getField37());
        userVO.setField38(user.getField38());
        userVO.setField39(user.getField39());
        userVO.setField40(user.getField40());
        userVO.setField41(user.getField41());
        userVO.setField42(user.getField42());
        userVO.setField43(user.getField43());
        userVO.setField44(user.getField44());
        userVO.setField45(user.getField45());
        userVO.setField46(user.getField46());
        userVO.setField47(user.getField47());
        userVO.setField48(user.getField48());
        userVO.setField49(user.getField49());
        userVO.setField50(user.getField50());
        userVO.setField51(user.getField51());
        userVO.setField52(user.getField52());
        userVO.setField53(user.getField53());
        userVO.setField54(user.getField54());
        userVO.setField55(user.getField55());
        userVO.setField56(user.getField56());
        userVO.setField57(user.getField57());
        userVO.setField58(user.getField58());
        userVO.setField59(user.getField59());
        userVO.setField60(user.getField60());
        userVO.setField61(user.getField61());
        userVO.setField62(user.getField62());
        userVO.setField63(user.getField63());
        userVO.setField64(user.getField64());
        userVO.setField65(user.getField65());
        userVO.setField66(user.getField66());
        userVO.setField67(user.getField67());
        userVO.setField68(user.getField68());
        userVO.setField69(user.getField69());
        userVO.setField70(user.getField70());
        userVO.setField71(user.getField71());
        userVO.setField72(user.getField72());
        userVO.setField73(user.getField73());
        userVO.setField74(user.getField74());
        userVO.setField75(user.getField75());
        userVO.setField76(user.getField76());
        userVO.setField77(user.getField77());
        userVO.setField78(user.getField78());
        userVO.setField79(user.getField79());
        userVO.setField80(user.getField80());
        userVO.setField81(user.getField81());
        userVO.setField82(user.getField82());
        userVO.setField83(user.getField83());
        userVO.setField84(user.getField84());
        userVO.setField85(user.getField85());
        userVO.setField86(user.getField86());
        userVO.setField87(user.getField87());
        userVO.setField88(user.getField88());
        userVO.setField89(user.getField89());
        userVO.setField90(user.getField90());
        userVO.setField91(user.getField91());
        userVO.setField92(user.getField92());
        userVO.setField93(user.getField93());
        userVO.setField94(user.getField94());
        userVO.setField95(user.getField95());
        userVO.setField96(user.getField96());
        userVO.setField97(user.getField97());
        userVO.setField98(user.getField98());
        userVO.setField99(user.getField99());
        
        assert "zzs0".equals(userVO.getName());
        return userVO;
    }
    
    
    /**
     * 测试apache BeanUtils拷贝对象属性
     * @author zzs
     * @date 2020年11月9日 下午3:52:51
     * @param user
     * @return UserVO
     * @throws Exception
     */
    @Benchmark
    public UserVO testApacheBeanUtils(CommonState commonState) throws Exception {
        UserVO userVO = new UserVO();
        org.apache.commons.beanutils.BeanUtils.copyProperties(userVO, commonState.user);
        assert "zzs0".equals(userVO.getName());
        return userVO;
    }
    
    /**
     * 测试spring BeanUtils拷贝对象属性
     * @author zzs
     * @date 2020年11月9日 下午4:08:53
     * @param user
     * @return UserVO
     * @throws Exception
     */
    @Benchmark
    public UserVO testSpringBeanUtils(CommonState commonState) throws Exception {
        UserVO userVO = new UserVO();
        org.springframework.beans.BeanUtils.copyProperties(commonState.user, userVO);
        assert "zzs0".equals(userVO.getName());
        return userVO;
    }
    
    /**
     * 测试cglib BeanCopier拷贝对象属性
     * @author zzs
     * @date 2020年11月9日 下午4:11:50
     * @param user
     * @return UserVO
     * @throws Exception
     */
    @Benchmark
    public UserVO testCglibBeanCopier(CommonState commonState, CglibBeanCopierState cglibBeanCopierState) throws Exception {
        BeanCopier copier = cglibBeanCopierState.copier;
        UserVO userVO = new UserVO();
        copier.copy(commonState.user, userVO, null);
        assert "zzs0".equals(userVO.getName());
        return userVO;
    }
    
    @State(Scope.Benchmark)
    public static class CglibBeanCopierState {
        BeanCopier copier;
        @Setup(Level.Trial)
        public void prepare() {
            copier = BeanCopier.create(User.class, UserVO.class, false);
        }
    }
    
    /**
     * 测试使用orika拷贝对象属性
     * @author zzs
     * @date 2020年11月9日 下午4:19:03
     * @param user
     * @return UserVO
     * @throws Exception
     */
    @Benchmark
    public UserVO testOrikaBeanCopy(CommonState commonState, OrikaState orikaState) throws Exception {
        MapperFacade mapperFacade = orikaState.mapperFactory.getMapperFacade();
        UserVO userVO = mapperFacade.map(commonState.user, UserVO.class);
        assert "zzs0".equals(userVO.getName());
        return userVO;
    }
    @State(Scope.Benchmark)
    public static class OrikaState {
        MapperFactory mapperFactory;
        @Setup(Level.Trial)
        public void prepare() {
            mapperFactory = new DefaultMapperFactory.Builder().build();
        }
    }
    
    public static void main(String[] args) {
        System.setProperty("cglib.debugLocation", "D:/growUp/test");
        
        
        User user = new UserService().get();
        BeanCopier copier = BeanCopier.create(user.getClass(), UserVO.class, false);
        UserVO userVO = new UserVO();
        copier.copy(user, userVO, null);
        assert "zzs0".equals(userVO.getName());
    }
    
}
