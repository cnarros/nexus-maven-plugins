/**
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.maven.staging.it.nxcm4527;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.maven.it.VerificationException;
import org.sonatype.nexus.maven.staging.it.PreparedVerifier;
import org.sonatype.nexus.maven.staging.it.SimpleRountripMatrixSupport;
import org.sonatype.nexus.mindexer.client.SearchResponse;

import com.sonatype.nexus.staging.client.StagingRepository;

/**
 * See NXCM-4527, this IT implements it's verification part for Nexus Staging Maven Plugin side. Here, we build and
 * stage a "malformed" project (will lack the javadoc JAR, achieved by passing in "-Dmaven.javadoc.skip=true" during
 * deploy). The project uses default settings for nexus-staging-maven-plugin, so such malformed staged project should
 * have the staging repository dropped upon rule failure. Hence, {@link #postNexusAssertions(PreparedVerifier)} contains
 * checks that there are no staging repositories but also that the artifact built is not released either.
 * 
 * @author cstamas
 */
public class NXCM4527DropOnCloseRuleFailureIT
    extends SimpleRountripMatrixSupport
{
    /**
     * no pre-invocation assertions
     */
    @Override
    protected void preNexusAssertions( final PreparedVerifier verifier )
    {
    }

    /**
     * Validates nexus side of affairs post maven invocations.
     */
    @Override
    protected void postNexusAssertions( final PreparedVerifier verifier )
    {
        // there are no staging repositories as we dropped them (on rule failure)
        final List<StagingRepository> stagingRepositories = getAllStagingRepositories();
        if ( !stagingRepositories.isEmpty() )
        {
            Assert.fail( "Nexus should not have staging repositories, but it has: " + stagingRepositories );
        }

        // stuff we staged should not be released and not found by indexer
        final SearchResponse searchResponse =
            searchThreeTimesForGAV( verifier.getProjectGroupId(), verifier.getProjectArtifactId(),
                verifier.getProjectVersion(), null, null, "releases" );
        if ( !searchResponse.getHits().isEmpty() )
        {
            Assert.fail( String.format(
                "Nexus should NOT have staged artifact in releases repository with GAV=%s:%s:%s but those are not found on index!",
                verifier.getProjectGroupId(), verifier.getProjectArtifactId(), verifier.getProjectVersion() ) );
        }
    }

    @Override
    protected void invokeMaven( final PreparedVerifier verifier )
        throws VerificationException
    {
        try
        {
            // skip javadoc, we want failing build
            verifier.getVerifier().addCliOption( "-Dmaven.javadoc.skip=true" );
            // v2 workflow
            verifier.getVerifier().executeGoals( Arrays.asList( "clean", "deploy" ) );
            // should fail
            verifier.getVerifier().verifyErrorFreeLog();
            // if no exception, fail the test
            Assert.fail( "We should end up with failed remote staging!" );
        }
        catch ( VerificationException e )
        {
            // good
        }
    }
}