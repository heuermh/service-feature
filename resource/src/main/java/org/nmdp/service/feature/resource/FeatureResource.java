/*

    feature-resource  Feature resources.
    Copyright (c) 2014-2015 National Marrow Donor Program (NMDP)

    This library is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 3 of the License, or (at
    your option) any later version.

    This library is distributed in the hope that it will be useful, but WITHOUT
    ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
    FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
    License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this library;  if not, write to the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

    > http://www.gnu.org/licenses/lgpl.html

*/
package org.nmdp.service.feature.resource;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import org.nmdp.service.feature.Feature;

import org.nmdp.service.feature.service.FeatureService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Feature resource.
 */
@Path("/features")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value="Features", description="Create and retrieve enumerated sequence features.")
@Immutable
public final class FeatureResource {
    private final FeatureService featureService;
    private static final Logger logger = LoggerFactory.getLogger(FeatureResource.class);

    @Inject
    public FeatureResource(final FeatureService featureService) {
        checkNotNull(featureService);
        this.featureService = featureService;
    }

    @GET
    @ApiOperation(value="Retrieve an enumerated sequence feature", response=Feature.class)
    @ApiResponses(value = {
            @ApiResponse(code=400, message="locus must be provided"),
            @ApiResponse(code=400, message="term must be provided"),
            @ApiResponse(code=400, message="rank must be provided and at least 1"),
            @ApiResponse(code=400, message="accession must be provided and at least 1"),
    })
    public Feature getFeatureByQuery(final @QueryParam("locus") @ApiParam("locus name or URI") String locus,
                                     final @QueryParam("term") @ApiParam("Sequence Ontology (SO) term name, accession, or URI") String term,
                                     final @QueryParam("rank") @ApiParam("feature rank, must be at least 1") int rank,
                                     final @QueryParam("accession") @ApiParam("accession, must be at least 1") long accession)
        throws UserInputException {

        if (logger.isTraceEnabled()) {
            logger.trace("getFeature locus " + locus + " term " + term + " rank " + rank + " accession " + accession);
        }

        // todo: returning null here sends HTTP 204 No Content which causes trouble for Retrofit
        Feature feature = null;
        try {
            feature = featureService.getFeature(locus, term, rank, accession);
        }
        catch (IllegalArgumentException e) {
            throw new UserInputException(e.getMessage(), e);
        }
        return feature;
    }

    @POST
    @ApiOperation(value="Create an enumerated sequence feature", response=Feature.class)
    @ApiImplicitParam(paramType="body", dataType="FeatureRequest")
    @ApiResponses(value = {
            @ApiResponse(code=400, message="a request body must be provided"),
    })
    public Feature createFeature(final FeatureRequest featureRequest) throws UserInputException {

        if (featureRequest == null) {
            throw new UserInputException("a request body must be provided", null);
        }
        if (logger.isTraceEnabled()) {
            logger.trace("createFeature locus " + featureRequest.getLocus() + " term " + featureRequest.getTerm() + " rank " + featureRequest.getRank() + " sequence " + featureRequest.getSequence());
        }

        Feature feature = null;
        try {
            feature = featureService.createFeature(featureRequest.getLocus(), featureRequest.getTerm(), featureRequest.getRank(), featureRequest.getSequence());
        }
        catch (IllegalArgumentException e) {
            throw new UserInputException(e.getMessage(), e);
        }
        return feature;
    }

    @GET
    @Path("{locus}")
    @ApiOperation(value="List the enumerated sequence features at a locus", response=Feature.class, responseContainer="List")
    @ApiResponses(value = {
            @ApiResponse(code=400, message="locus must be provided"),
    })
    public List<Feature> listFeatures(final @PathParam("locus") @ApiParam("locus name or URI") String locus)
        throws UserInputException {

        List<Feature> features = null;
        try {
            features = featureService.listFeatures(locus);
        }
        catch (IllegalArgumentException e) {
            throw new UserInputException(e.getMessage(), e);
        }
        return features;
    }

    @GET
    @Path("{locus}/{term}")
    @ApiOperation(value="List the enumerated sequence features matching a term at a locus", response=Feature.class, responseContainer="List")
    @ApiResponses(value = {
            @ApiResponse(code=400, message="locus must be provided"),
            @ApiResponse(code=400, message="term must be provided"),
    })
    public List<Feature> listFeatures(final @PathParam("locus") @ApiParam("locus name or URI") String locus,
                                      final @PathParam("term") @ApiParam("Sequence Ontology (SO) term name, accession, or URI") String term)
        throws UserInputException {

        List<Feature> features = null;
        try {
            features = featureService.listFeatures(locus, term);
        }
        catch (IllegalArgumentException e) {
            throw new UserInputException(e.getMessage(), e);
        }
        return features;
    }

    @GET
    @Path("{locus}/{term}/{rank}")
    @ApiOperation(value="List the enumerated sequence features matching a term and rank at a locus", response=Feature.class, responseContainer="List")
    @ApiResponses(value = {
            @ApiResponse(code=400, message="locus must be provided"),
            @ApiResponse(code=400, message="term must be provided"),
            @ApiResponse(code=400, message="rank must be provided and at least 1"),
    })
    public List<Feature> listFeatures(final @PathParam("locus") @ApiParam("locus name or URI") String locus,
                                      final @PathParam("term") @ApiParam("Sequence Ontology (SO) term name, accession, or URI") String term,
                                      final @PathParam("rank") @ApiParam("feature rank, must be at least 1") int rank)
        throws UserInputException {

        List<Feature> features = null;
        try {
            features = featureService.listFeatures(locus, term, rank);
        }
        catch (IllegalArgumentException e) {
            throw new UserInputException(e.getMessage(), e);
        }
        return features;
    }

    @GET
    @Path("{locus}/{term}/{rank}/{accession}")
    @ApiOperation(value="Retrieve an enumerated sequence feature", response=Feature.class)
    @ApiResponses(value = {
            @ApiResponse(code=400, message="locus must be provided"),
            @ApiResponse(code=400, message="term must be provided"),
            @ApiResponse(code=400, message="rank must be provided and at least 1"),
            @ApiResponse(code=400, message="accession must be provided and at least 1"),
    })
    public Feature getFeatureByPath(final @PathParam("locus") @ApiParam("locus name or URI") String locus,
                                    final @PathParam("term") @ApiParam("Sequence Ontology (SO) term name, accession, or URI") String term,
                                    final @PathParam("rank") @ApiParam("feature rank, must be at least 1") int rank,
                                    final @PathParam("accession") @ApiParam("accession, must be at least 1") long accession)
        throws UserInputException {

        // todo: returning null here sends HTTP 204 No Content which causes trouble for Retrofit
        Feature feature = null;
        try {
            feature = featureService.getFeature(locus, term, rank, accession);
        }
        catch (IllegalArgumentException e) {
            throw new UserInputException(e.getMessage(), e);
        }
        return feature;
    }
}
