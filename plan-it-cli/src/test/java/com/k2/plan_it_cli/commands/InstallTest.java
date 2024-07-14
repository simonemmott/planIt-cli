package com.k2.plan_it_cli.commands;

import com.k2.plan_it_cli.home.PlanItHome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallTest {

    @InjectMocks
    Install sut;

    @Test
    public void shouldConstruct() {
        // Then
        assertNotNull(sut);
    }

    @Test
    public void shouldCheckHomeDirAndSetupHomeOnInstall() {
        // Given
        File home = mock(File.class);
        doReturn("HOME").when(home).toString();
        try(MockedStatic<PlanItHome> planItHome = mockStatic(PlanItHome.class)) {
            planItHome.when(() -> PlanItHome.checkHomeDir(home))
                    .thenReturn(true);
            planItHome.when(() -> PlanItHome.setup(System.out, home))
                    .thenReturn(true);

            // Then When
            assertEquals("Installed PlanIt home dir in HOME", sut.install(home));
        }
    }
}
