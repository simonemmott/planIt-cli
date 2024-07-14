package com.k2.plan_it_cli.home;

import com.k2.plan_it_cli.config.PlanItConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanItHomeTest {

    PlanItHome sut;

    @Test
    public void shouldConstructWithoutPlanItHomeEnvVarSet() {
        // Given
        try(MockedStatic<PlanItHome> planItHomeMockedStatic = mockStatic(PlanItHome.class)) {
            planItHomeMockedStatic.when(() -> PlanItHome.getHomeEnv())
                    .thenReturn(null);

            // When
            sut = new PlanItHome();

            // Then
            assertNull(sut.home);
            assertNull(sut.bin);
            assertNull(sut.config);
            assertNull(sut.plans);
        }
    }

    @Test
    public void shouldConstructWithPlanItHomeEnvVarSet() {
        // Given
        File home = new File("HOME");
        try(MockedStatic<PlanItHome> planItHomeMockedStatic = mockStatic(PlanItHome.class)) {
            planItHomeMockedStatic.when(() -> PlanItHome.getHomeEnv())
                    .thenReturn("HOME");

            // When
            sut = new PlanItHome();

            // Then
            assertEquals(home, sut.home);
            assertEquals(new File(home, "bin"), sut.bin);
            assertEquals(new File(home, "config"), sut.config);
            assertEquals(new File(home, "plans"), sut.plans);
        }
    }

    @Test
    public void shouldCheckHomeAndPrintOutputOnReport() {
        // Given
        File home = new File("HOME");
        PrintStream out = mock(PrintStream.class);
        try(MockedStatic<PlanItHome> planItHomeMockedStatic = mockStatic(PlanItHome.class)) {
            planItHomeMockedStatic.when(() -> PlanItHome.getHomeEnv())
                    .thenReturn("HOME");

            sut = new PlanItHome();
            PlanItHome spy = spy(sut);
            doNothing().when(spy).checkHome();

            // When
            spy.report(out);

            // Then
            verify(spy).checkHome();
            verify(out).println();
            verify(out, times(6)).println(anyString());
        }
    }

    @Test
    public void shouldCThrowWhenCheckHomeThrowsAndNotOutputOnReport() {
        // Given
        File home = new File("HOME");
        PrintStream out = mock(PrintStream.class);
        try(MockedStatic<PlanItHome> planItHomeMockedStatic = mockStatic(PlanItHome.class)) {
            planItHomeMockedStatic.when(() -> PlanItHome.getHomeEnv())
                    .thenReturn("HOME");

            sut = new PlanItHome();
            PlanItHome spy = spy(sut);
            IllegalStateException err = new IllegalStateException("ERR");
            doThrow(err).when(spy).checkHome();

            // Then When
            assertThrows(IllegalStateException.class, () -> spy.report(out));
            verify(spy).checkHome();
            verify(out, times(0)).println();
            verify(out, times(0)).println(anyString());
        }
    }

    @Test
    public void shouldThrowWhenHomeIsNullOnCheckHome() {
        // Given
        sut = new PlanItHome(null, null, null, null);

        // Then When
        IllegalStateException err = assertThrows(IllegalStateException.class, () -> sut.checkHome());
        assertEquals(MessageFormat.format(
                "No {0} home directory defined", PlanItConstants.APP_NAME
        ), err.getMessage());
    }

    @Test
    public void shouldThrowWhenHomeDoesNotExistOnCheckHome() {
        // Given
        File home = mock(File.class);
        doReturn(false).when(home).exists();
        doReturn("HOME").when(home).toString();
        sut = new PlanItHome(home, null, null, null);

        // Then When
        IllegalStateException err = assertThrows(IllegalStateException.class, () -> sut.checkHome());
        assertEquals(MessageFormat.format(
                "The {0} home directory HOME does not exist", PlanItConstants.APP_NAME
        ), err.getMessage());
    }

    @Test
    public void shouldThrowWhenBinDoesNotExistOnCheckHome() {
        // Given
        File home = mock(File.class);
        doReturn(true).when(home).exists();
        File bin = mock(File.class);
        doReturn(false).when(bin).exists();
        doReturn("BIN").when(bin).toString();
        sut = new PlanItHome(home, bin, null, null);

        // Then When
        IllegalStateException err = assertThrows(IllegalStateException.class, () -> sut.checkHome());
        assertEquals(MessageFormat.format(
                "The {0} bin directory BIN does not exist", PlanItConstants.APP_NAME
        ), err.getMessage());
    }

    @Test
    public void shouldThrowWhenConfigDoesNotExistOnCheckHome() {
        // Given
        File home = mock(File.class);
        doReturn(true).when(home).exists();
        File bin = mock(File.class);
        doReturn(true).when(bin).exists();
        File config = mock(File.class);
        doReturn(false).when(config).exists();
        doReturn("CONFIG").when(config).toString();
        sut = new PlanItHome(home, bin, config, null);

        // Then When
        IllegalStateException err = assertThrows(IllegalStateException.class, () -> sut.checkHome());
        assertEquals(MessageFormat.format(
                "The {0} config directory CONFIG does not exist", PlanItConstants.APP_NAME
        ), err.getMessage());
    }

    @Test
    public void shouldThrowWhenPlansDoesNotExistOnCheckHome() {
        // Given
        File home = mock(File.class);
        doReturn(true).when(home).exists();
        File bin = mock(File.class);
        doReturn(true).when(bin).exists();
        File config = mock(File.class);
        doReturn(true).when(config).exists();
        File plans = mock(File.class);
        doReturn(false).when(plans).exists();
        doReturn("PLANS").when(plans).toString();
        sut = new PlanItHome(home, bin, config, plans);

        // Then When
        IllegalStateException err = assertThrows(IllegalStateException.class, () -> sut.checkHome());
        assertEquals(MessageFormat.format(
                "The {0} plans directory PLANS does not exist", PlanItConstants.APP_NAME
        ), err.getMessage());
    }

    @Test
    public void shouldNotThrowWhenHomeOkOnCheckHome() {
        // Given
        File home = mock(File.class);
        doReturn(true).when(home).exists();
        File bin = mock(File.class);
        doReturn(true).when(bin).exists();
        File config = mock(File.class);
        doReturn(true).when(config).exists();
        File plans = mock(File.class);
        doReturn(true).when(plans).exists();
        sut = new PlanItHome(home, bin, config, plans);

        // Then When
        assertDoesNotThrow(() -> sut.checkHome());
    }

    @Test
    public void shouldCheckHomeAndReturnBinOnGetBin() {
        // Given
        File home = mock(File.class);
        File bin = mock(File.class);
        File config = mock(File.class);
        File plans = mock(File.class);
        sut = new PlanItHome(home, bin, config, plans);
        PlanItHome spy = spy(sut);
        doNothing().when(spy).checkHome();

        // Then When
        assertEquals(bin, spy.getBin());
        verify(spy).checkHome();

    }

    @Test
    public void shouldCheckHomeAndReturnConfigOnGetConfig() {
        // Given
        File home = mock(File.class);
        File bin = mock(File.class);
        File config = mock(File.class);
        File plans = mock(File.class);
        sut = new PlanItHome(home, bin, config, plans);
        PlanItHome spy = spy(sut);
        doNothing().when(spy).checkHome();

        // Then When
        assertEquals(config, spy.getConfig());
        verify(spy).checkHome();

    }

    @Test
    public void shouldCheckHomeAndReturnPlansOnGetPlans() {
        // Given
        File home = mock(File.class);
        File bin = mock(File.class);
        File config = mock(File.class);
        File plans = mock(File.class);
        sut = new PlanItHome(home, bin, config, plans);
        PlanItHome spy = spy(sut);
        doNothing().when(spy).checkHome();

        // Then When
        assertEquals(plans, spy.getPlans());
        verify(spy).checkHome();

    }


}
