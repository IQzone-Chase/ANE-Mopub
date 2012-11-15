//
//  MoPubInterstitial.m
//  MoPubIosExtension
//
//  Created by Richard Lord on 14/11/2012.
//  Copyright (c) 2012 Richard Lord. All rights reserved.
//

#import "MoPubInterstitial.h"
#import "MoPubInternalMessages.h"

@interface MoPubInterstitial ()
{
}
@property (nonatomic, assign)FREContext context;
@property (nonatomic, assign)MPInterstitialAdController* interstitial;

@end

@implementation MoPubInterstitial

@synthesize context, interstitial;

- (id) initWithContext:(FREContext)extensionContext adUnitId:(NSString*)adUnitId
{
    self = [super init];
    
    if( self )
    {
        self.context = extensionContext;
        self.interstitial = [MPInterstitialAdController interstitialAdControllerForAdUnitId:adUnitId];
        self.interstitial.delegate = self;
    }
    return self;
}

- (void)dealloc
{
    self.interstitial.delegate = nil;
    self.interstitial = nil;
    [super dealloc];
}

- (BOOL) getIsReady
{
    return self.interstitial.ready;
}

- (void) setTesting:(BOOL)value
{
    self.interstitial.testing = value;
}

- (void) loadInterstitial
{
    [self.interstitial loadAd];
}

- (BOOL) showInterstitial
{
    if( self.interstitial.ready )
    {
        [self.interstitial showFromViewController:[UIApplication sharedApplication].keyWindow.rootViewController];
        return YES;
    }
    return NO;
}

- (void)interstitialDidLoadAd:(MPInterstitialAdController *)interstitial
{
    FREDispatchStatusEventAsync( context, "", interstitialLoaded );
}

- (void)interstitialDidFailToLoadAd:(MPInterstitialAdController *)interstitial
{
    FREDispatchStatusEventAsync( context, "", interstitialFailedToLoad );
}

- (void)interstitialDidDisappear:(MPInterstitialAdController *)interstitial
{
    FREDispatchStatusEventAsync( context, "", interstitialClosed );
}

- (void)interstitialDidExpire:(MPInterstitialAdController *)interstitial
{
    FREDispatchStatusEventAsync( context, "", interstitialExpired );
}

@end
