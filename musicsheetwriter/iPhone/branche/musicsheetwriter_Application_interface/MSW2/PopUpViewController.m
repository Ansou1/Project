//
//  PopUpViewController.m
//  MSW2
//
//  Created by simon on 10/10/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "PopUpViewController.h"

@interface PopUpViewController ()

@end

@implementation PopUpViewController

- (void) popup{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Vous devez être connecté à internet." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
    [alert show];
}

@end
