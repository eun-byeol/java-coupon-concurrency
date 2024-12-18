package coupon.service;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import coupon.domain.Coupon;
import coupon.repository.CouponRepository;
import coupon.support.Fixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CouponServiceTest {

    private static final String COUPON_CACHE_NAME = "coupon";

    @Autowired
    private CouponService couponService;

    @SpyBean
    private CacheManager cacheManager;

    @SpyBean
    private CouponRepository couponRepository;

    @Test
    void 복제지연테스트() {
        Coupon saved = couponService.save(Fixture.createCoupon());

        assertThatCode(() -> couponService.findById(saved.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void 조회_시_캐시에_없으면_DB_조회_후_캐시_쓰기한다() {
        Coupon dbCoupon = couponService.save(Fixture.createCoupon());

        Coupon firstSearch = couponService.findById(dbCoupon.getId());

        Cache couponCache = requireNonNull(cacheManager.getCache(COUPON_CACHE_NAME));

        assertAll(
                () -> assertThat(firstSearch.getId()).isEqualTo(dbCoupon.getId()),
                () -> verify(couponRepository, atLeastOnce()).findById(dbCoupon.getId()),
                () -> assertThat(couponCache.get(dbCoupon.getId(), Coupon.class))
                        .extracting(Coupon::getId)
                        .isEqualTo(dbCoupon.getId())
        );
    }

    @Test
    void 조회_시_캐시에_있으면_DB를_조회하지_않는다() {
        Coupon dbCoupon = couponService.save(Fixture.createCoupon());
        couponService.findById(dbCoupon.getId());

        clearInvocations(couponRepository);

        Coupon secondSearch = couponService.findById(dbCoupon.getId());

        assertAll(
                () -> assertThat(secondSearch.getId()).isEqualTo(dbCoupon.getId()),
                () -> verify(couponRepository, never()).findById(dbCoupon.getId())
        );
    }
}
