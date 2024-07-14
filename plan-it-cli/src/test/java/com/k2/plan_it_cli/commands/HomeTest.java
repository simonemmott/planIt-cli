package com.k2.plan_it_cli.commands;

import com.k2.plan_it_cli.home.PlanItHome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HomeTest {

    @Mock
    PlanItHome planItHome;

    @InjectMocks
    Home sut;

    @Test
    public void shouldConstructWithPlanItHome() {
        // Then
        assertNotNull(sut);
        assertEquals(planItHome, sut.getPlanItHome());
    }

    @Test
    public void shouldPresentPlanItHomeReportOnShow() {
        // Then When
        assertEquals("", sut.show());
        verify(planItHome).report(System.out);
    }
}
