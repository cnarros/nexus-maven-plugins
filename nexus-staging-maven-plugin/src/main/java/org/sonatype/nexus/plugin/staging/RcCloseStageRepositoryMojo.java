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
package org.sonatype.nexus.plugin.staging;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.sonatype.nexus.client.srv.staging.StagingWorkflowV2Service;

/**
 * Closes a Nexus staging repository.
 * 
 * @goal rc-close
 * @requiresProject false
 */
public class RcCloseStageRepositoryMojo
    extends AbstractStagingRcActionMojo
{
    @Override
    public void doExecute( final StagingWorkflowV2Service stagingWorkflow )
        throws MojoExecutionException, MojoFailureException
    {
        stagingWorkflow.finishStagingRepositories( getDescription(), getStagingRepositoryIds() );
    }
}
