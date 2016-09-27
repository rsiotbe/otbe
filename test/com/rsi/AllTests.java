package com.rsi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import com.rsi.isum.IsumValidationTest;
import com.rsi.rvia.rest.session.SessionRviaDataTest;

@RunWith(Suite.class)
@SuiteClasses({ IsumValidationTest.class, SessionRviaDataTest.class })
public class AllTests
{
}
