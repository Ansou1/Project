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

- (void) popupId:(id)object{
    UIAlertController * view2 = [UIAlertController
                                 alertControllerWithTitle:@""
                                 message:@"Vous devez être connecté à internet."
                                 preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* butt1 = [UIAlertAction
                            actionWithTitle:@"Fermer"
                            style:UIAlertActionStyleDefault
                            handler:^(UIAlertAction * action)
                            {
                                [view2 dismissViewControllerAnimated:YES completion:nil];
                            }];
    [view2 addAction:butt1];
    [self presentViewController:view2 animated:YES completion:nil];
}

@end
